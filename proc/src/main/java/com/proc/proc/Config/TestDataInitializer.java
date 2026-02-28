package com.proc.proc.Config;

import com.proc.proc.Model.JobPosting;
import com.proc.proc.Repository.JobPostingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private JobPostingRepo jobPostingRepo;

    @Override
    public void run(String... args) {
        if (jobPostingRepo.count() == 0) {
            createTestJobs();
            System.out.println(" Test data created: " + jobPostingRepo.count() + " jobs");
        }
    }

    private void createTestJobs() {
        JobPosting job1 = new JobPosting();
        job1.setTitle("Java Developer");
        job1.setCompany("TCS");
        job1.setLocation("Bangalore");
        job1.setSalary("8-12 LPA");
        job1.setExperience("3-5 years");
        job1.setJobType("Full-time");
        job1.setSkills("Java, Spring Boot, MySQL, AWS");
        job1.setDescription("Looking for experienced Java developer with Spring Boot knowledge");
        job1.setApplicationLink("https://example.com/job1");
        job1.setSource("Test");
        job1.setPostedDate(LocalDateTime.now());
        jobPostingRepo.save(job1);

        JobPosting job2 = new JobPosting();
        job2.setTitle("Python Developer");
        job2.setCompany("Infosys");
        job2.setLocation("Mumbai");
        job2.setSalary("10-15 LPA");
        job2.setExperience("4-6 years");
        job2.setJobType("Full-time");
        job2.setSkills("Python, Django, PostgreSQL, Docker");
        job2.setDescription("Python developer needed for backend development");
        job2.setApplicationLink("https://example.com/job2");
        job2.setSource("Test");
        job2.setPostedDate(LocalDateTime.now());
        jobPostingRepo.save(job2);

        JobPosting job3 = new JobPosting();
        job3.setTitle("Full Stack Developer");
        job3.setCompany("Google");
        job3.setLocation("Bangalore");
        job3.setSalary("20-30 LPA");
        job3.setExperience("5-8 years");
        job3.setJobType("Full-time");
        job3.setSkills("React, Node.js, MongoDB, AWS, Docker");
        job3.setDescription("Full stack developer for cloud-native applications");
        job3.setApplicationLink("https://example.com/job3");
        job3.setSource("Test");
        job3.setPostedDate(LocalDateTime.now());
        jobPostingRepo.save(job3);
    }
}
