# JobHunt - Implementation Plan

## Overview
Transform Cognitron web crawler into JobHunt job aggregation platform.

**Timeline:** 7 days  
**Team Size:** 1 developer

---

## Phase 1: Job-Specific Data Model (Day 1)

### Tasks

#### 1.1 Create JobPosting Entity
**File:** `proc/src/main/java/com/proc/proc/Model/JobPosting.java`
- [ ] Add fields: title, company, location, salary, experience, jobType, skills, description, applicationLink, source
- [ ] Add JPA annotations
- [ ] Add indexes for search optimization

**Estimated Time:** 1 hour

#### 1.2 Create JobPosting Repository
**File:** `proc/src/main/java/com/proc/proc/Repository/JobPostingRepo.java`
- [ ] Extend JpaRepository
- [ ] Add custom queries: findByTitleAndLocation, findBySkillsContaining, findRecentJobs

**Estimated Time:** 30 minutes

#### 1.3 Create CrawlHistory Entity
**File:** `proc/src/main/java/com/proc/proc/Model/CrawlHistory.java`
- [ ] Track search_key, last_crawled, jobs_found
- [ ] Add repository

**Estimated Time:** 30 minutes

#### 1.4 Update Database Schema
- [ ] Run application to auto-create tables
- [ ] Verify schema in MySQL

**Estimated Time:** 30 minutes

**Phase 1 Total:** 2.5 hours

---

## Phase 2: Job Site Registry (Day 1-2)

### Tasks

#### 2.1 Create JobSiteRegistry Service
**File:** `crawl/src/main/java/com/dp/crawl/Service/JobSiteRegistry.java`
- [ ] Define URL templates for LinkedIn, Indeed, Naukri, Glassdoor
- [ ] Implement buildSearchUrls(jobTitle, location) method
- [ ] Add URL encoding logic

**Estimated Time:** 1 hour

#### 2.2 Create JobSearchRequest Model
**File:** `crawl/src/main/java/com/dp/crawl/Model/JobSearchRequest.java`
- [ ] Fields: jobTitle, location, experience, minSalary, maxSalary
- [ ] Add validation annotations

**Estimated Time:** 30 minutes

#### 2.3 Update CrawlController
**File:** `crawl/src/main/java/com/dp/crawl/Controller/CrawlController.java`
- [ ] Add POST /api/jobs/search endpoint
- [ ] Accept JobSearchRequest
- [ ] Generate URLs using JobSiteRegistry
- [ ] Publish multiple CrawlMessages

**Estimated Time:** 1 hour

#### 2.4 Test URL Generation
- [ ] Test with sample inputs
- [ ] Verify URLs are correct

**Estimated Time:** 30 minutes

**Phase 2 Total:** 3 hours

---

## Phase 3: Job Extraction Logic (Day 2-3)

### Tasks

#### 3.1 Create JobExtractionService
**File:** `proc/src/main/java/com/proc/proc/Service/JobExtractionService.java`
- [ ] Implement extractJobFromHtml(Document, String source)
- [ ] Add site-specific CSS selectors (LinkedIn, Indeed, Naukri)
- [ ] Extract: title, company, location, salary
- [ ] Fallback to generic extraction if selectors fail

**Estimated Time:** 3 hours

#### 3.2 Create SkillExtractor
**File:** `proc/src/main/java/com/proc/proc/Service/SkillExtractor.java`
- [ ] Define skill keywords (Java, Python, AWS, React, etc.)
- [ ] Implement extractSkills(String text) using regex
- [ ] Return comma-separated skills

**Estimated Time:** 1 hour

#### 3.3 Update CrawlMessageListener
**File:** `proc/src/main/java/com/proc/proc/Listener/CrawlMessageListener.java`
- [ ] Call JobExtractionService after fetching HTML
- [ ] Save to JobPosting instead of CrawledPage
- [ ] Extract skills from description

**Estimated Time:** 1 hour

#### 3.4 Test Extraction
- [ ] Crawl sample job pages
- [ ] Verify data extraction accuracy
- [ ] Fix selector issues

**Estimated Time:** 2 hours

**Phase 3 Total:** 7 hours

---

## Phase 4: Job Search API (Day 3-4)

### Tasks

#### 4.1 Create JobController
**File:** `proc/src/main/java/com/proc/proc/Controller/JobController.java`
- [ ] GET /api/jobs - list all jobs
- [ ] GET /api/jobs/{id} - get job details
- [ ] GET /api/jobs/search?title=&location= - search jobs
- [ ] GET /api/jobs/filter - advanced filtering

**Estimated Time:** 2 hours

#### 4.2 Create JobService
**File:** `proc/src/main/java/com/proc/proc/Service/JobService.java`
- [ ] Implement search logic
- [ ] Implement filtering (location, skills, salary)
- [ ] Implement sorting (date, relevance)
- [ ] Add pagination

**Estimated Time:** 2 hours

#### 4.3 Add CORS Configuration
**File:** `proc/src/main/java/com/proc/proc/Config/CorsConfig.java`
- [ ] Allow React frontend (port 3000)
- [ ] Configure allowed methods and headers

**Estimated Time:** 30 minutes

#### 4.4 Test APIs with Postman
- [ ] Test all endpoints
- [ ] Verify response format
- [ ] Check error handling

**Estimated Time:** 1 hour

**Phase 4 Total:** 5.5 hours

---

## Phase 5: Background Scheduler (Day 4)

### Tasks

#### 5.1 Update CrawlScheduler
**File:** `crawl/src/main/java/com/dp/crawl/Service/CrawlScheduler.java`
- [ ] Define popular job titles (Software Engineer, Data Scientist, etc.)
- [ ] Define popular locations (Bangalore, Mumbai, Delhi, etc.)
- [ ] Schedule crawl every 6 hours
- [ ] Generate URLs for all combinations

**Estimated Time:** 1 hour

#### 5.2 Add Scheduling Configuration
**File:** `crawl/src/main/java/com/dp/crawl/CrawlApplication.java`
- [ ] Add @EnableScheduling annotation

**Estimated Time:** 10 minutes

#### 5.3 Test Scheduler
- [ ] Reduce interval to 1 minute for testing
- [ ] Verify jobs are crawled automatically
- [ ] Check database population

**Estimated Time:** 30 minutes

**Phase 5 Total:** 1.5 hours

---

## Phase 6: React Dashboard (Day 5-6)

### Tasks

#### 6.1 Setup React Project
- [ ] Create React app: `npm create vite@latest`
- [ ] Install dependencies: axios, react-router-dom, tailwindcss
- [ ] Configure Tailwind CSS

**Estimated Time:** 1 hour

#### 6.2 Create Components
**Files:** `dashboard/src/components/`
- [ ] SearchBar.jsx - job title + location input
- [ ] JobCard.jsx - display single job
- [ ] JobList.jsx - display all jobs
- [ ] FilterPanel.jsx - location, skills, salary filters
- [ ] Header.jsx - navigation
 
**Estimated Time:** 3 hours

#### 6.3 Implement Search Functionality
- [ ] Call POST /api/jobs/search on form submit
- [ ] Display loading state
- [ ] Show results in JobList

**Estimated Time:** 2 hours

#### 6.4 Implement Filtering
- [ ] Add filter controls
- [ ] Call GET /api/jobs/filter with params
- [ ] Update JobList

**Estimated Time:** 2 hours

#### 6.5 Styling & Responsiveness
- [ ] Mobile-friendly design
- [ ] Clean UI with Tailwind
- [ ] Add icons and colors

**Estimated Time:** 2 hours

#### 6.6 Deploy Frontend
- [ ] Build production bundle
- [ ] Serve via Spring Boot or separate server

**Estimated Time:** 1 hour

**Phase 6 Total:** 11 hours

---

## Phase 7: Testing & Bug Fixes (Day 6-7)

### Tasks

#### 7.1 End-to-End Testing
- [ ] Test complete flow: search → crawl → display
- [ ] Test with different job titles and locations
- [ ] Verify deduplication works
- [ ] Check error handling

**Estimated Time:** 2 hours

#### 7.2 Performance Testing
- [ ] Test with 500+ jobs in database
- [ ] Measure search response time
- [ ] Optimize slow queries

**Estimated Time:** 1 hour

#### 7.3 Bug Fixes
- [ ] Fix extraction issues
- [ ] Handle edge cases
- [ ] Improve error messages

**Estimated Time:** 2 hours

#### 7.4 Code Cleanup
- [ ] Remove unused code
- [ ] Add comments
- [ ] Format code consistently

**Estimated Time:** 1 hour

**Phase 7 Total:** 6 hours

---

## Phase 8: Documentation & Demo (Day 7)

### Tasks

#### 8.1 Update README
- [ ] Add JobHunt description
- [ ] Add setup instructions
- [ ] Add API documentation
- [ ] Add screenshots

**Estimated Time:** 1 hour

#### 8.2 Create Demo Video
- [ ] Record screen showing search flow
- [ ] Show dashboard features
- [ ] Explain architecture
- [ ] Upload to YouTube/Loom

**Estimated Time:** 1 hour

#### 8.3 Prepare Interview Talking Points
- [ ] Write architecture explanation
- [ ] Prepare design decision justifications
- [ ] List challenges and solutions

**Estimated Time:** 1 hour

**Phase 8 Total:** 3 hours

---

## Summary

| Phase | Duration | Key Deliverable |
|-------|----------|----------------|
| Phase 1 | 2.5 hours | Job data model |
| Phase 2 | 3 hours | URL generation |
| Phase 3 | 7 hours | Job extraction |
| Phase 4 | 5.5 hours | Search API |
| Phase 5 | 1.5 hours | Auto-crawling |
| Phase 6 | 11 hours | React dashboard |
| Phase 7 | 6 hours | Testing & fixes |
| Phase 8 | 3 hours | Documentation |

**Total Estimated Time:** 40 hours (~7 working days)

---

## Daily Breakdown

**Day 1:** Phase 1 + Phase 2 (5.5 hours)  
**Day 2:** Phase 3 (7 hours)  
**Day 3:** Phase 3 completion + Phase 4 start (5.5 hours)  
**Day 4:** Phase 4 completion + Phase 5 (7 hours)  
**Day 5:** Phase 6 start (6 hours)  
**Day 6:** Phase 6 completion + Phase 7 start (8 hours)  
**Day 7:** Phase 7 completion + Phase 8 (6 hours)

---

## Risk Mitigation

**Risk:** Job site HTML changes  
**Mitigation:** Build flexible extractors with fallbacks

**Risk:** Rate limiting by job sites  
**Mitigation:** Add delays, respect robots.txt

**Risk:** Database performance issues  
**Mitigation:** Add indexes, implement pagination

---

**Plan Status:** Ready for Execution  
**Next Step:** Begin Phase 1
