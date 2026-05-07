package com.proc.proc.Service;

import com.proc.proc.Model.CrawledPage;
import com.proc.proc.Repository.CrawledPageRepo;
import org.springframework.stereotype.Service;

@Service
public class CrawledPageService {

    private final CrawledPageRepo crawledPageRepo;

    public CrawledPageService(CrawledPageRepo crawledPageRepo) {
        this.crawledPageRepo = crawledPageRepo;
    }

    public void save(CrawledPage page) {
        crawledPageRepo.save(page);
    }
}
