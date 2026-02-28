package com.proc.proc.Controller;

import com.proc.proc.Model.JobPosting;
import com.proc.proc.Repository.JobPostingRepo;
import com.proc.proc.Service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    @Autowired
    private JobPostingRepo jobPostingRepo;
    
    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<?> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy) {
        
        if (page >= 0 && size > 0) {
            Page<JobPosting> jobsPage = jobService.getAllJobsPaginated(page, size, sortBy);
            return ResponseEntity.ok(jobsPage);
        }
        
        List<JobPosting> jobs = jobPostingRepo.findAll();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJobById(@PathVariable Long id) {
        Optional<JobPosting> job = jobPostingRepo.findById(id);
        return job.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobPosting>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills) {
        
        List<JobPosting> jobs = jobService.searchJobs(title, location, skills);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<JobPosting>> filterJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String minSalary) {
        
        List<JobPosting> jobs = jobService.filterByMultipleCriteria(location, skills, company, minSalary);
        return ResponseEntity.ok(jobs);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<JobPosting>> getRecentJobs(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<JobPosting> jobs = jobService.getRecentJobs(limit);
        return ResponseEntity.ok(jobs);
    }
}
