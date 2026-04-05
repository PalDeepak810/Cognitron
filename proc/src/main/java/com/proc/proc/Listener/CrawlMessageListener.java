package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Model.JobPosting;
import com.proc.proc.Model.VisitedUrl;
import com.proc.proc.Repository.JobPostingRepo;
import com.proc.proc.Repository.VisitedUrlRepo;
import com.proc.proc.Service.*;
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

    @Autowired
    private SeleniumService seleniumService;

    @Autowired
    private CrawlQuotaService crawlQuotaService;

    @RabbitListener(queues = "content-crawl-queue")
    public void processMessage(CrawlMessage msg) {

        System.out.println(">>> Listener invoked");

        if (msg == null || msg.getUrl() == null || msg.getConfig() == null) {
            return;
        }

        System.out.println(">>> Received URL: " + msg.getUrl() + " | runId=" + msg.getRunId());

        // DEPTH CHECK
        if (msg.getDepth() > msg.getConfig().getMaxDepth()) {
            System.out.println(">>> Max depth reached");
            return;
        }

        // DEDUP
        String hash = VisitedUrl.sha256(msg.getUrl());
        if (visitedUrlRepo.existsByUrlHash(hash)) {
            System.out.println(">>> Duplicate URL");
            return;
        }

        // GLOBAL 24H BUDGET CHECK
        if (crawlQuotaService.remainingGlobalBudget() <= 0) {
            System.out.println(">>> Global 24h crawl budget reached | runId=" + msg.getRunId());
            return;
        }

        // PER-RUN BUDGET CHECK
        if (!crawlQuotaService.tryAcquireRunSlot(msg.getRunId(), msg.getRunPageLimit())) {
            System.out.println(">>> Run page limit reached | runId=" + msg.getRunId());
            return;
        }

        visitedUrlRepo.save(new VisitedUrl(msg.getUrl(), hash));

        //FETCH
        Document doc;
        try {
            if (seleniumService.isSeleniumRequired(msg.getUrl())) {
                System.out.println(">>> Using Selenium for: " + msg.getUrl());
                doc = seleniumService.fetchDynamicPage(msg.getUrl());
                if (doc == null) {
                    System.out.println(">>> Selenium fetch failed, skipping");
                    return;
                }
            } else {
                doc = Jsoup.connect(msg.getUrl())
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(15000)
                        .get();
            }
        } catch (Exception e) {
            System.out.println(">>> Fetch failed: " + e.getMessage());
            return;
        }

        msg.setTitle(doc.title());
        msg.setHtml(doc.html());
        msg.setText(doc.body() != null ? doc.body().text() : "");

        if (!linkExtractorService.isRelevant(msg)) {
            return;
        }

        // JOB EXTRACTION
        try {
            JobPosting job = jobExtractionService.extractJobFromHtml(doc, msg.getUrl());
            
            if (job.getTitle() != null && job.getCompany() != null && job.getLocation() != null) {
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
                    System.out.println("✓ Job saved: " + job.getTitle() + " at " + job.getCompany());
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Job extraction failed: " + e.getMessage());
        }

        crawledPageService.save(msg);

        // LINK DISCOVERY
        Set<String> links = linkExtractorService.extractLinks(msg.getHtml());

        Set<String> filteredLinks = linkExtractorService.filterLinks(links, msg);

        long remainingRunBudget = crawlQuotaService.remainingRunBudget(msg.getRunId(), msg.getRunPageLimit());
        long remainingGlobalBudget = crawlQuotaService.remainingGlobalBudget();
        long remainingBudget = Math.min(remainingRunBudget, remainingGlobalBudget);

        for (String link : filteredLinks) {
            if (remainingBudget <= 0) {
                break;
            }
            CrawlMessage child = new CrawlMessage();
            child.setUrl(link);
            child.setDepth(msg.getDepth() + 1);
            child.setParentUrl(msg.getUrl());
            child.setConfig(msg.getConfig());
            child.setRunId(msg.getRunId());
            child.setRunPageLimit(msg.getRunPageLimit());

            discoveredLinkPublisher.publish(child);
            remainingBudget--;
        }
    }


}
