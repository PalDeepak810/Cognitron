package com.proc.proc.Service;

import com.proc.proc.Model.JobPosting;
import com.proc.proc.Model.JobSubscription;
import com.proc.proc.Repository.JobPostingRepo;
import com.proc.proc.Repository.JobSubscriptionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class JobAlertScheduler {

    @Autowired
    private JobSubscriptionRepo subscriptionRepo;
    
    @Autowired
    private JobPostingRepo jobPostingRepo;
    
    @Autowired
    private EmailService emailService;
    
    // Send daily digest at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyJobAlerts() {
        System.out.println(">>> Sending daily job alerts...");
        
        List<JobSubscription> activeSubscriptions = subscriptionRepo.findByActiveTrue();
        
        for (JobSubscription subscription : activeSubscriptions) {
            try {
                List<JobPosting> matchingJobs = findMatchingJobs(subscription);
                
                if (!matchingJobs.isEmpty()) {
                    emailService.sendJobAlerts(subscription.getEmail(), matchingJobs);
                }
            } catch (Exception e) {
                System.out.println("✗ Failed to send alert to: " + subscription.getEmail());
            }
        }
        
        System.out.println(">>> Daily alerts completed for " + activeSubscriptions.size() + " subscribers");
    }
    
    private List<JobPosting> findMatchingJobs(JobSubscription subscription) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<JobPosting> recentJobs = jobPostingRepo.findRecentJobsSince(yesterday);
        
        // Filter by user preferences
        if (subscription.getJobTitles() != null && !subscription.getJobTitles().isEmpty()) {
            List<String> preferredTitles = Arrays.asList(subscription.getJobTitles().split(","));
            recentJobs = recentJobs.stream()
                .filter(job -> preferredTitles.stream()
                    .anyMatch(title -> job.getTitle().toLowerCase().contains(title.trim().toLowerCase())))
                .toList();
        }
        
        if (subscription.getLocations() != null && !subscription.getLocations().isEmpty()) {
            List<String> preferredLocations = Arrays.asList(subscription.getLocations().split(","));
            recentJobs = recentJobs.stream()
                .filter(job -> preferredLocations.stream()
                    .anyMatch(loc -> job.getLocation().toLowerCase().contains(loc.trim().toLowerCase())))
                .toList();
        }
        
        return recentJobs.stream().limit(10).toList(); // Max 10 jobs per email
    }
}
