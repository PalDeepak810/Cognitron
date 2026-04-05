package com.proc.proc.Service;

import com.proc.proc.Model.JobPosting;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class JobExtractionService {

    public JobPosting extractJobFromHtml(Document doc, String url) {
        String source = detectSource(url);
        
        JobPosting job = new JobPosting();
        job.setSource(source);
        job.setApplicationLink(url);
        
        switch (source) {
            case "Naukri":
                extractFromNaukri(doc, job);
                break;
            case "Indeed":
                extractFromIndeed(doc, job);
                break;
            case "LinkedIn":
                extractFromLinkedIn(doc, job);
                break;
            default:
                extractGeneric(doc, job);
        }
        
        return job;
    }
    
    private String detectSource(String url) {
        if (url.contains("naukri.com")) return "Naukri";
        if (url.contains("indeed.com")) return "Indeed";
        if (url.contains("linkedin.com")) return "LinkedIn";
        return "Unknown";
    }
    
    private void extractFromNaukri(Document doc, JobPosting job) {
        // Search result page selectors
        job.setTitle(getText(doc, ".title"));
        job.setCompany(getText(doc, ".comp-name"));
        job.setLocation(getText(doc, ".location"));
        job.setSalary(getText(doc, ".salary"));
        job.setExperience(getText(doc, ".experience"));
        
        // Individual job page selectors (fallback)
        if (job.getTitle() == null) {
            job.setTitle(getText(doc, ".jd-header-title"));
        }
        if (job.getCompany() == null) {
            job.setCompany(getText(doc, ".jd-header-comp-name"));
        }
        if (job.getLocation() == null) {
            job.setLocation(getText(doc, ".loc"));
        }
        if (job.getDescription() == null) {
            job.setDescription(getText(doc, ".job-desc"));
        }
        
        // Final fallback
        if (job.getTitle() == null) {
            job.setTitle(doc.title());
        }
        if (job.getCompany() == null) {
            job.setCompany("Unknown Company");
        }
        if (job.getLocation() == null) {
            job.setLocation("Unknown Location");
        }
    }
    
    private void extractFromIndeed(Document doc, JobPosting job) {
        // Individual job page selectors
        job.setTitle(getText(doc, "h1.jobsearch-JobInfoHeader-title"));
        job.setCompany(getText(doc, "[data-company-name='true']"));
        job.setLocation(getText(doc, "[data-testid='job-location']"));
        job.setDescription(getText(doc, "#jobDescriptionText"));
        
        // Search result page selectors (fallback)
        if (job.getTitle() == null) {
            job.setTitle(getText(doc, ".jobTitle"));
        }
        if (job.getCompany() == null) {
            job.setCompany(getText(doc, ".companyName"));
        }
        if (job.getLocation() == null) {
            job.setLocation(getText(doc, ".companyLocation"));
        }
        
        // Final fallback
        if (job.getTitle() == null) {
            job.setTitle(doc.title());
        }
        if (job.getCompany() == null) {
            job.setCompany("Unknown Company");
        }
        if (job.getLocation() == null) {
            job.setLocation("Unknown Location");
        }
    }
    
    private void extractFromLinkedIn(Document doc, JobPosting job) {
        // Individual job page selectors
        job.setTitle(getText(doc, ".top-card-layout__title"));
        job.setCompany(getText(doc, ".topcard__org-name-link"));
        job.setLocation(getText(doc, ".topcard__flavor--bullet"));
        job.setDescription(getText(doc, ".show-more-less-html__markup"));
        
        // Search result page selectors (fallback)
        if (job.getTitle() == null) {
            job.setTitle(getText(doc, ".base-search-card__title"));
        }
        if (job.getCompany() == null) {
            job.setCompany(getText(doc, ".base-search-card__subtitle"));
        }
        if (job.getLocation() == null) {
            job.setLocation(getText(doc, ".job-search-card__location"));
        }
        
        // Extract job type from metadata
        String jobCriteria = getText(doc, ".description__job-criteria-text");
        if (jobCriteria != null) {
            if (jobCriteria.contains("Full-time")) job.setJobType("Full-time");
            else if (jobCriteria.contains("Part-time")) job.setJobType("Part-time");
            else if (jobCriteria.contains("Contract")) job.setJobType("Contract");
        }
        
        // Final fallback
        if (job.getTitle() == null) {
            job.setTitle(doc.title());
        }
        if (job.getCompany() == null) {
            job.setCompany("Unknown Company");
        }
        if (job.getLocation() == null) {
            job.setLocation("Unknown Location");
        }
    }
    
    private void extractGeneric(Document doc, JobPosting job) {
        job.setTitle(doc.title());
        job.setDescription(doc.body().text());
    }
    
    private String getText(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text().trim() : null;
    }
}
