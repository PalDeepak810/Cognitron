package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Service.CrawledPageService;
import com.proc.proc.Service.DiscoveredLinkPublisher;
import com.proc.proc.Service.LinkExtractorService;
import com.proc.proc.Service.TextProcessorService;
import com.proc.proc.Repository.VisitedUrlRepo;
import com.proc.proc.Model.VisitedUrl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CrawlMessageListener {

    @Autowired
    private TextProcessorService textProcessorService;

    @Autowired
    private CrawledPageService crawledPageService;

    @Autowired
    private LinkExtractorService linkExtractorService;

    @Autowired
    private DiscoveredLinkPublisher discoveredLinkPublisher;

    @Autowired
    private VisitedUrlRepo visitedUrlRepository;

    @RabbitListener(queues = "content-crawl-queue")
    public void processMessage(CrawlMessage message) {

        String url = message.getUrl();
        System.out.println("Received message for URL: " + url);

        if (visitedUrlRepository.existsById(url)) {
            System.out.println("Skipping already visited: " + url);
            return;
        }


        visitedUrlRepository.save(new VisitedUrl(url));


        String cleanedText = textProcessorService.cleanText(message.getHtml());
        message.setText(cleanedText);
        crawledPageService.save(message);

        System.out.println("Saved page: " + url);
        System.out.println("Cleaned text length: " + cleanedText.length());


        Set<String> extractedLinks = linkExtractorService.extractLinks(message.getHtml());
        System.out.println("Extracted raw links: " + extractedLinks.size());


        Set<String> filteredLinks = linkExtractorService.filterLinks(extractedLinks);
        System.out.println("Filtered links: " + filteredLinks.size());


        filteredLinks.forEach(link -> {
            if (!visitedUrlRepository.existsById(link)) {
                discoveredLinkPublisher.publish(link);
            }
        });
    }
}
