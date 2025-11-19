package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Service.CrawledPageService;
import com.proc.proc.Service.TextProcessorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlMessageListener {

   @Autowired
   private TextProcessorService textProcessorService;

   @Autowired
   private CrawledPageService crawledPageService;

    @RabbitListener(queues = "content-crawl-queue")
    public void processMessage(CrawlMessage message) {
        System.out.println("Received message for URL: " + message.getUrl());
        String cleanedText = textProcessorService.cleanText(message.getHtml());
        message.setText(cleanedText);

        crawledPageService.save(message);

        System.out.println("saved page:"+message.getUrl());
        System.out.println("Cleaned text length:"+cleanedText.length());
    }
}
