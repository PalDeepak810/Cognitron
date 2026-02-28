# JobHunt - Product Requirements Document (PRD)

## 1. Product Overview

**Product Name:** JobHunt  
**Version:** 1.0  
**Type:** Job Aggregation Platform

### 1.1 Problem Statement
Job seekers waste hours searching across LinkedIn, Indeed, Naukri, Glassdoor separately. No unified view of opportunities.

### 1.2 Solution
Automated crawler that aggregates jobs from multiple portals into one searchable dashboard.

---

## 2. Target Users

**Primary:** Fresh graduates and experienced professionals actively job hunting  
**Secondary:** Recruiters monitoring market trends

---

## 3. Core Features

### 3.1 Job Search (MVP)
**User Story:** As a job seeker, I want to search for jobs by title and location, so I can find all relevant opportunities in one place.

**Acceptance Criteria:**
- User enters job title (e.g., "Software Engineer")
- User enters location (e.g., "Bangalore")
- System searches LinkedIn, Indeed, Naukri, Glassdoor
- Results displayed within 30 seconds
- Minimum 50 jobs per search

---

### 3.2 Job Display
**User Story:** As a user, I want to see job details clearly, so I can quickly evaluate opportunities.

**Acceptance Criteria:**
- Display: Title, Company, Location, Salary, Skills
- "Apply" button opens original job link
- Posted date shown
- Source site indicated (LinkedIn/Indeed/etc)

---

### 3.3 Filtering
**User Story:** As a user, I want to filter jobs, so I can find the most relevant ones.

**Filters:**
- Location (dropdown)
- Experience level (0-2, 2-5, 5+ years)
- Job type (Remote, Hybrid, Onsite)
- Salary range (slider)
- Skills (multi-select)

---

### 3.4 Background Crawling
**User Story:** As a user, I want fresh job listings, so I don't miss new opportunities.

**Acceptance Criteria:**
- System crawls popular job titles every 6 hours
- Pre-populated database with 1000+ jobs
- User sees instant results for common searches

---

## 4. Non-Functional Requirements

### 4.1 Performance
- Search results: < 2 seconds (cached)
- Fresh crawl: < 60 seconds
- Support 100 concurrent users

### 4.2 Scalability
- Handle 10,000 jobs in database
- Crawl 500 pages per search
- Horizontal scaling support

### 4.3 Reliability
- 99% uptime
- Graceful failure handling
- No duplicate jobs

### 4.4 Usability
- Mobile responsive
- Intuitive search interface
- One-click apply

---

## 5. Technical Constraints

- Must respect robots.txt
- 15-second timeout per page
- Max depth: 2 levels
- Max pages per search: 500

---

## 6. Success Metrics

- **Adoption:** 100 users in first month
- **Engagement:** 5 searches per user per week
- **Coverage:** 80% of jobs from top 4 sites
- **Freshness:** Jobs updated every 6 hours

---

## 7. Future Features (Post-MVP)

### Phase 2
- Email alerts for new matching jobs
- Save favorite jobs
- Application tracking

### Phase 3
- Resume upload + AI matching
- Salary insights and trends
- Company reviews integration

### Phase 4
- Mobile app (iOS/Android)
- Chrome extension
- API for third-party integration

---

## 8. Out of Scope (v1.0)

- User authentication
- Resume builder
- Direct application submission
- Interview scheduling
- Salary negotiation tools

---

## 9. Launch Plan

### Beta Launch
- Target: 20 beta users (friends/classmates)
- Duration: 2 weeks
- Goal: Gather feedback, fix bugs

### Public Launch
- Platform: ProductHunt, Reddit, LinkedIn
- Goal: 100 users in first month

---

## 10. Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Job sites block crawler | High | Rotate user agents, respect rate limits |
| HTML structure changes | Medium | Site-specific extractors, fallback logic |
| Database grows too large | Medium | Archive old jobs (>30 days) |
| Slow crawl speed | Low | Horizontal scaling, caching |

---

**Document Owner:** Product Team  
**Last Updated:** 2024  
**Status:** Approved
