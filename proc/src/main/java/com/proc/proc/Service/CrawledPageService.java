package com.proc.proc.Service;

import com.proc.proc.Model.CrawlMessage;
import com.proc.proc.Model.CrawledPage;
import com.proc.proc.Repository.CrawledPageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CrawledPageService {

    @Autowired
    private CrawledPageRepo crawledPageRepo;


    public void save(CrawlMessage message) {
        CrawledPage page = new CrawledPage();

        page.setUrl(message.getUrl());
        page.setTitle(message.getTitle());
        page.setHtml(message.getHtml());
        page.setText(message.getText());
        page.setSource(message.getSource());
        page.setCrawledAt(LocalDateTime.now());

        crawledPageRepo.save(page);
    }
}
