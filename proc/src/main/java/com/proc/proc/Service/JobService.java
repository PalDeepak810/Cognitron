package com.proc.proc.Service;

import com.proc.proc.Model.JobPosting;
import com.proc.proc.Repository.JobPostingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobPostingRepo jobPostingRepo;

    public Page<JobPosting> getAllJobsPaginated(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy != null ? sortBy : "postedDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        return jobPostingRepo.findAll(pageable);
    }

    public List<JobPosting> searchJobs(String title, String location, String skills) {
        // Handle empty strings as null
        final String finalTitle = (title != null && title.trim().isEmpty()) ? null : title;
        final String finalLocation = (location != null && location.trim().isEmpty()) ? null : location;
        final String finalSkills = (skills != null && skills.trim().isEmpty()) ? null : skills;

        List<JobPosting> results;

        if (finalTitle != null && finalLocation != null) {
            results = jobPostingRepo.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(finalTitle, finalLocation);
        } else if (finalTitle != null) {
            results = jobPostingRepo.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(finalTitle, "");
        } else if (finalLocation != null) {
            results = jobPostingRepo.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase("", finalLocation);
        } else {
            results = jobPostingRepo.findAll();
        }

        if (finalSkills != null) {
            results = results.stream()
                    .filter(job -> job.getSkills() != null && 
                           job.getSkills().toLowerCase().contains(finalSkills.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<JobPosting> filterByMultipleCriteria(String location, String skills, 
                                                      String company, String minSalary) {
        List<JobPosting> results = jobPostingRepo.findAll();

        if (location != null && !location.isEmpty()) {
            results = results.stream()
                    .filter(job -> job.getLocation() != null && 
                           job.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (skills != null && !skills.isEmpty()) {
            results = results.stream()
                    .filter(job -> job.getSkills() != null && 
                           job.getSkills().toLowerCase().contains(skills.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (company != null && !company.isEmpty()) {
            results = results.stream()
                    .filter(job -> job.getCompany() != null && 
                           job.getCompany().equalsIgnoreCase(company))
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<JobPosting> getRecentJobs(int limit) {
        List<JobPosting> allRecent = jobPostingRepo.findRecentJobs();
        return allRecent.stream().limit(limit).collect(Collectors.toList());
    }
}
