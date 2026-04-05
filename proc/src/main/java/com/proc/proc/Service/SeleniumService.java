package com.proc.proc.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SeleniumService {

    public Document fetchDynamicPage(String url) {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            
            driver.get(url);
            
            // Wait for JavaScript to load
            Thread.sleep(3000);
            
            String pageSource = driver.getPageSource();
            return Jsoup.parse(pageSource);
            
        } catch (Exception e) {
            System.out.println("✗ Selenium fetch failed: " + e.getMessage());
            return null;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    public boolean isSeleniumRequired(String url) {
        return url.contains("naukri.com") || 
               url.contains("indeed.com") || 
               url.contains("glassdoor.com");
    }
}
