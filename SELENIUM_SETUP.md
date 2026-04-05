# Selenium Setup Instructions

## ChromeDriver Installation

### Windows:

**Option 1: Automatic (Recommended)**
```bash
# Download ChromeDriver automatically
# Selenium 4.6+ includes Selenium Manager that auto-downloads drivers
# No manual setup needed!
```

**Option 2: Manual**
1. Check Chrome version: `chrome://version`
2. Download matching ChromeDriver: https://chromedriver.chromium.org/downloads
3. Extract `chromedriver.exe`
4. Add to PATH or place in project root

### Verify Installation:
```bash
chromedriver --version
```

## Configuration

### Enable Selenium for Specific Sites
Edit `SeleniumService.java`:
```java
public boolean isSeleniumRequired(String url) {
    return url.contains("naukri.com") || 
           url.contains("indeed.com") || 
           url.contains("glassdoor.com");
}
```

### Headless Mode (Default)
- Runs Chrome without GUI
- Faster, less resource-intensive
- Perfect for servers

### Debug Mode (See Browser)
Remove `--headless` in `SeleniumService.java`:
```java
ChromeOptions options = new ChromeOptions();
// options.addArguments("--headless"); // Comment this line
```

## Features:

✅ **Automatic fallback**: Uses Selenium only for JavaScript-heavy sites
✅ **Headless mode**: No browser window, runs in background
✅ **Wait for JavaScript**: 3-second delay for page load
✅ **User agent spoofing**: Looks like real browser
✅ **Auto-cleanup**: Driver quits after each request

## Sites Using Selenium:
- ✅ Naukri.com (JavaScript-rendered)
- ✅ Indeed.com (Anti-bot protection)
- ✅ Glassdoor (Optional)

## Sites Using Jsoup (Faster):
- ✅ LinkedIn
- ✅ Other static sites

## Performance:
- **Jsoup**: ~1-2 seconds per page
- **Selenium**: ~5-8 seconds per page (includes JS rendering)

## Troubleshooting:

**"ChromeDriver not found"**
- Selenium 4.6+ auto-downloads drivers
- Ensure internet connection for first run

**"Chrome version mismatch"**
- Update Chrome browser
- Or download matching ChromeDriver manually

**"Timeout errors"**
- Increase timeout in `SeleniumService.java`:
```java
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
Thread.sleep(5000); // Wait 5 seconds
```

**"Memory issues"**
- Selenium uses ~200MB per instance
- Driver auto-quits after each request
- Consider reducing MAX_PAGES if needed

## Testing:

Run proc service and check logs:
```
>>> Using Selenium for: https://www.naukri.com/...
✓ Job saved: Software Engineer at TCS
```

## Production Considerations:

1. **Docker**: Use `selenium/standalone-chrome` image
2. **Scaling**: Consider Selenium Grid for parallel crawling
3. **Rate Limiting**: Add delays between requests
4. **Proxy Rotation**: Avoid IP bans (future enhancement)
