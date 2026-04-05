package com.proc.proc.Controller;

import com.proc.proc.Model.JobSubscription;
import com.proc.proc.Repository.JobSubscriptionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private JobSubscriptionRepo subscriptionRepo;
    
    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody JobSubscription subscription) {
        Optional<JobSubscription> existing = subscriptionRepo.findByEmail(subscription.getEmail());
        
        if (existing.isPresent()) {
            JobSubscription existingSub = existing.get();
            existingSub.setJobTitles(subscription.getJobTitles());
            existingSub.setLocations(subscription.getLocations());
            existingSub.setActive(true);
            subscriptionRepo.save(existingSub);
            return ResponseEntity.ok(existingSub);
        }
        
        JobSubscription saved = subscriptionRepo.save(subscription);
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/{email}")
    public ResponseEntity<?> unsubscribe(@PathVariable String email) {
        Optional<JobSubscription> subscription = subscriptionRepo.findByEmail(email);
        
        if (subscription.isPresent()) {
            JobSubscription sub = subscription.get();
            sub.setActive(false);
            subscriptionRepo.save(sub);
            return ResponseEntity.ok("Unsubscribed successfully");
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{email}")
    public ResponseEntity<?> getSubscription(@PathVariable String email) {
        return subscriptionRepo.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
