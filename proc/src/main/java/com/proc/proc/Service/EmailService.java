package com.proc.proc.Service;

import com.proc.proc.Model.JobPosting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendJobAlerts(String toEmail, List<JobPosting> jobs) {
        if (jobs.isEmpty()) {
            return;
        }
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Hello,\n\n");
        emailBody.append("Here are the latest job opportunities matching your preferences:\n\n");
        
        for (JobPosting job : jobs) {
            emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            emailBody.append("📌 ").append(job.getTitle()).append("\n");
            emailBody.append("🏢 ").append(job.getCompany()).append("\n");
            emailBody.append("📍 ").append(job.getLocation()).append("\n");
            
            if (job.getSalary() != null) {
                emailBody.append("💰 ").append(job.getSalary()).append("\n");
            }
            if (job.getExperience() != null) {
                emailBody.append("⏳ ").append(job.getExperience()).append("\n");
            }
            if (job.getSkills() != null) {
                emailBody.append("🔧 Skills: ").append(job.getSkills()).append("\n");
            }
            
            emailBody.append("🔗 Apply: ").append(job.getApplicationLink()).append("\n\n");
        }
        
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("Total Jobs: ").append(jobs.size()).append("\n\n");
        emailBody.append("Best regards,\nJobHunt Team");
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("🎯 JobHunt Alert: " + jobs.size() + " New Jobs Found!");
        message.setText(emailBody.toString());
        
        try {
            mailSender.send(message);
            System.out.println("✓ Email sent to: " + toEmail);
        } catch (Exception e) {
            System.out.println("✗ Email failed for: " + toEmail + " - " + e.getMessage());
        }
    }
}
