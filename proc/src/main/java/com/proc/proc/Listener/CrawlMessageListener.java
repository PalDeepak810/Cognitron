package com.proc.proc.Listener;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Service.TextProcessorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CrawlMessageListener {

    private final TextProcessorService textProcessorService;

    public CrawlMessageListener(TextProcessorService textProcessorService) {
        this.textProcessorService = textProcessorService;
    }

    @RabbitListener(queues = "content-crawl-queue")
    public void processMessage(CrawlMessage message) {
        System.out.println("Received message for URL: " + message.getUrl());
        String cleanedText = textProcessorService.cleanText(message.getHtml());
        message.setText(cleanedText);

        // Todo
        System.out.println("Cleaned text length: " + cleanedText.length());
    }
}
