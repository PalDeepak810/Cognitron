# JobHunt - Technical Architecture Document

## 1. Executive Summary

**JobHunt** is a distributed job aggregation platform that automatically crawls multiple job portals, extracts job postings, and presents them in a unified dashboard.

**Core Value:** One search, all jobs.

---

## 2. System Architecture

```
User Browser (React)
       ↓
Crawl Service (8081) → RabbitMQ → Processor Service (8082) → MySQL
       ↑                                    ↓
       └────────────────────────────────────┘
```

---

## 3. Components

### 3.1 Crawl Service
- Accept job search (title, location)
- Generate URLs for job sites
- Publish to RabbitMQ

### 3.2 Processor Service
- Fetch HTML (Jsoup)
- Extract job data
- Store in MySQL
- Discover new links

### 3.3 RabbitMQ
- content-crawl-queue
- discovered-links-queue

### 3.4 MySQL
- job_postings
- visited_urls
- crawl_history

---

## 4. Data Flow

1. User searches "Backend Engineer in Bangalore"
2. System generates URLs for LinkedIn, Indeed, Naukri
3. Publishes to content-crawl-queue
4. Processor fetches, extracts, stores
5. Discovers pagination links
6. Re-queues to discovered-links-queue
7. Loop continues until maxDepth

---

## 5. Technology Stack

- Java 17
- Spring Boot 3.x
- RabbitMQ
- MySQL 8
- Jsoup
- React (Dashboard)

---

## 6. Scalability

- Stateless services
- Horizontal scaling via multiple instances
- Queue-based decoupling
- SHA256 deduplication

---

**Version:** 1.0
