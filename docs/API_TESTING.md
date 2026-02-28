# JobController API Testing Guide

## Prerequisites
1. Start the Processor service (proc)
2. Ensure MySQL is running
3. Test data will be auto-created on startup

## Base URL
```
http://localhost:8081/api/jobs
```

---

## Test Endpoints

### 1. Get All Jobs
```bash
curl http://localhost:8081/api/jobs
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "title": "Java Developer",
    "company": "TCS",
    "location": "Bangalore",
    "salary": "8-12 LPA",
    "skills": "Java, Spring Boot, MySQL, AWS",
    ...
  }
]
```

---

### 2. Get Job by ID
```bash
curl http://localhost:8081/api/jobs/1
```

**Expected Response:**
```json
{
  "id": 1,
  "title": "Java Developer",
  "company": "TCS",
  ...
}
```

---

### 3. Search by Title
```bash
curl "http://localhost:8081/api/jobs/search?title=Java"
```

**Expected:** Returns jobs with "Java" in title

---

### 4. Search by Location
```bash
curl "http://localhost:8081/api/jobs/search?location=Bangalore"
```

**Expected:** Returns jobs in Bangalore

---

### 5. Search by Title AND Location
```bash
curl "http://localhost:8081/api/jobs/search?title=Developer&location=Bangalore"
```

**Expected:** Returns developer jobs in Bangalore

---

### 6. Filter by Skills
```bash
curl "http://localhost:8081/api/jobs/filter?skills=AWS"
```

**Expected:** Returns jobs requiring AWS skills

---

### 7. Filter by Company
```bash
curl "http://localhost:8081/api/jobs/filter?company=Google"
```

**Expected:** Returns Google jobs

---

### 8. Get Recent Jobs
```bash
curl http://localhost:8081/api/jobs/recent
```

**Expected:** Returns jobs sorted by posted date (newest first)

---

## Testing in Browser

Open these URLs in your browser:

1. All jobs: `http://localhost:8081/api/jobs`
2. Search: `http://localhost:8081/api/jobs/search?title=Java&location=Bangalore`
3. Filter: `http://localhost:8081/api/jobs/filter?skills=AWS`
4. Recent: `http://localhost:8081/api/jobs/recent`

---

## Expected Test Results

✅ **GET /api/jobs** → Returns 3 test jobs  
✅ **GET /api/jobs/1** → Returns Java Developer job  
✅ **GET /api/jobs/search?title=Java** → Returns 1 job  
✅ **GET /api/jobs/search?location=Bangalore** → Returns 2 jobs  
✅ **GET /api/jobs/filter?skills=AWS** → Returns 2 jobs  
✅ **GET /api/jobs/filter?company=Google** → Returns 1 job  
✅ **GET /api/jobs/recent** → Returns 3 jobs sorted by date  

---

## Troubleshooting

**Issue:** Empty response `[]`
- Check if test data was created (look for "✅ Test data created" in logs)
- Verify database connection
- Check table exists: `SHOW TABLES;` in MySQL

**Issue:** 404 Not Found
- Verify service is running on port 8081
- Check endpoint URL is correct

**Issue:** 500 Internal Server Error
- Check application logs
- Verify all dependencies are injected
- Check database schema matches entity

---

## Next Steps

After successful testing:
1. ✅ Phase 4.1 Complete
2. Move to Phase 4.2: Create JobService (optional for MVP)
3. Move to Phase 4.3: Add CORS Configuration
4. Move to Phase 5: Background Scheduler
