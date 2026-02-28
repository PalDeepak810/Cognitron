package com.proc.proc.Controller;

import com.proc.proc.Model.CrawledPage;
import com.proc.proc.Repository.CrawledPageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.util.List;

@RestController
@RequestMapping("/api/pages")
public class CrawledPageController {

    @Autowired
    private CrawledPageRepo crawledPageRepo;

    @GetMapping
    public List<CrawledPage> getAll(){
        return crawledPageRepo.findAll();
    }

    @GetMapping("/{id}")
    public CrawledPage getOne(@PathVariable Long id){
        return crawledPageRepo.findById(id).orElse(null);
    }


    @GetMapping("/search")
    public Page<CrawledPage> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        return crawledPageRepo.searchByKeyword(
                keyword,
                PageRequest.of(page, 10, Sort.by("crawledAt").descending())
        );
    }

    @GetMapping("/search/title")
    public Page<CrawledPage> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page
    ) {
        return crawledPageRepo.findByTitleContainingIgnoreCaseOrderByCrawledAtDesc(
                title,
                PageRequest.of(page, 10, Sort.by("crawledAt").descending())
        );
    }


}
