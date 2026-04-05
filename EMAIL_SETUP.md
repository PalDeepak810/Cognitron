# Email Alert Setup Instructions

## Gmail Configuration Steps:

### 1. Enable 2-Factor Authentication
- Go to: https://myaccount.google.com/security
- Enable "2-Step Verification"

### 2. Generate App Password
- Go to: https://myaccount.google.com/apppasswords
- Select "Mail" and "Other (Custom name)"
- Name it "JobHunt"
- Copy the 16-character password

### 3. Update application.yml
Replace in `proc/src/main/resources/application.yml`:
```yaml
spring:
  mail:
    username: your-email@gmail.com        # Your Gmail address
    password: xxxx xxxx xxxx xxxx         # 16-char App Password (no spaces)
```

### 4. Test Email Alerts

**Option 1: Manual Test (Immediate)**
```bash
# In proc service, add this test endpoint temporarily
curl -X POST http://localhost:8082/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "jobTitles": "Software Engineer,Data Scientist",
    "locations": "Bangalore,Mumbai"
  }'
```

**Option 2: Wait for Daily Digest**
- Scheduled to run daily at 9:00 AM
- Sends jobs from last 24 hours

**Option 3: Trigger Manually (For Demo)**
Change cron in `JobAlertScheduler.java`:
```java
@Scheduled(fixedRate = 60000) // Every 1 minute for testing
```

## API Endpoints:

### Subscribe
```bash
POST http://localhost:8082/api/subscriptions
{
  "email": "user@example.com",
  "jobTitles": "Software Engineer,Full Stack Developer",
  "locations": "Bangalore,Pune"
}
```

### Unsubscribe
```bash
DELETE http://localhost:8082/api/subscriptions/user@example.com
```

### Get Subscription
```bash
GET http://localhost:8082/api/subscriptions/user@example.com
```

## Email Format:
- Subject: "🎯 JobHunt Alert: X New Jobs Found!"
- Contains: Job title, company, location, salary, experience, skills, apply link
- Max 10 jobs per email

## Troubleshooting:

**"Authentication failed"**
- Use App Password, not regular Gmail password
- Remove spaces from App Password

**"No emails received"**
- Check spam folder
- Verify subscription is active
- Ensure jobs exist in last 24 hours

**"Daily digest not working"**
- Cron runs at 9 AM daily
- For testing, change to `fixedRate = 60000` (1 minute)
