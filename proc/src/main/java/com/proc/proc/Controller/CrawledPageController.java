package com.proc.proc.Controller;

import com.proc.proc.Model.CrawledPage;
import com.proc.proc.Repository.CrawledPageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
