package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Model.JobPosting;
import com.proc.proc.Model.VisitedUrl;
import com.proc.proc.Repository.JobPostingRepo;
import com.proc.proc.Repository.VisitedUrlRepo;
import com.proc.proc.Service.CrawledPageService;
import com.proc.proc.Service.DiscoveredLinkPublisher;
import com.proc.proc.Service.JobExtractionService;
import com.proc.proc.Service.LinkExtractorService;
import com.proc.proc.Service.SkillExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class CrawlMessageListener {

    @Autowired
    private CrawledPageService crawledPageService;

    @Autowired
    private LinkExtractorService linkExtractorService;

    @Autowired
    private DiscoveredLinkPublisher discoveredLinkPublisher;

    @Autowired
    private JobExtractionService jobExtractionService;
    
    @Autowired
    private SkillExtractor skillExtractor;
    
    @Autowired
    private JobPostingRepo jobPostingRepo;

    @Autowired
    private VisitedUrlRepo visitedUrlRepo;

    @RabbitListener(queues = "content-crawl-queue")
    public void processMessage(CrawlMessage msg) {

        System.out.println(">>> Listener invoked");

        if (msg == null || msg.getUrl() == null || msg.getConfig() == null) {
            return;
        }

        System.out.println(">>> Received URL: " + msg.getUrl());

        // DEPTH CHECK
        if (msg.getDepth() > msg.getConfig().getMaxDepth()) {
            System.out.println(">>> Max depth reached");
            return;
        }

        //  MAX PAGES CHECK (SAFE)
        Integer maxPages = msg.getConfig().getMaxPages();
        long crawledCount = visitedUrlRepo.count();

        if (maxPages != null && maxPages > 0 && crawledCount >= maxPages) {
            System.out.println(">>> Max pages reached");
            return;
        }

        // DEDUP
        String hash = VisitedUrl.sha256(msg.getUrl());
        if (visitedUrlRepo.existsByUrlHash(hash)) {
            System.out.println(">>> Duplicate URL");
            return;
        }

        visitedUrlRepo.save(new VisitedUrl(msg.getUrl(), hash));

        //FETCH
        Document doc;
        try {
            doc = Jsoup.connect(msg.getUrl())
                    .userAgent("Cognitron-Processor/1.0")
                    .timeout(15000)
                    .get();
        } catch (Exception e) {
            System.out.println(">>> Fetch failed");
            return;
        }

        msg.setTitle(doc.title());
        msg.setHtml(doc.html());
        msg.setText(doc.body() != null ? doc.body().text() : "");

        if (!linkExtractorService.isRelevant(msg)) {
            System.out.println(">>> Irrelevant page skipped");
            return;
        }

        // JOB EXTRACTION
        try {
            JobPosting job = jobExtractionService.extractJobFromHtml(doc, msg.getUrl());
            
            if (job.getTitle() != null && job.getCompany() != null) {
                // Extract skills from description
                if (job.getDescription() != null) {
                    String skills = skillExtractor.extractSkills(job.getDescription());
                    job.setSkills(skills);
                }
                
                // Check for duplicate job posting
                Optional<JobPosting> existing = jobPostingRepo.findBySourceAndApplicationLink(
                    job.getSource(), job.getApplicationLink()
                );
                
                if (existing.isEmpty()) {
                    jobPostingRepo.save(job);
                    System.out.println(">>> Job saved: " + job.getTitle());
                } else {
                    System.out.println(">>> Duplicate job skipped");
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Job extraction failed: " + e.getMessage());
        }

        crawledPageService.save(msg);

        // LINK DISCOVERY
        Set<String> links = linkExtractorService.extractLinks(msg.getHtml());

        Set<String> filteredLinks = linkExtractorService.filterLinks(links, msg);

        for (String link : filteredLinks) {

            if (visitedUrlRepo.count() >= msg.getConfig().getMaxPages()) {
                break;
            }
            CrawlMessage child = new CrawlMessage();
            child.setUrl(link);
            child.setDepth(msg.getDepth() + 1);
            child.setParentUrl(msg.getUrl());
            child.setConfig(msg.getConfig());

            discoveredLinkPublisher.publish(child);
        }
    }


}
