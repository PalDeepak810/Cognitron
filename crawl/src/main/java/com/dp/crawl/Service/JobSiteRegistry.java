package com.dp.crawl.Service;

import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobSiteRegistry {

    public List<String> buildSearchUrls(String jobTitle, String location) {
        List<String> urls = new ArrayList<>();
        
        String encodedTitle = encodeUrl(jobTitle);
        String encodedLocation = encodeUrl(location);
        
        urls.add(buildNaukriUrl(encodedTitle, encodedLocation));
        urls.add(buildIndeedUrl(encodedTitle, encodedLocation)); // Re-enabled with Selenium
        urls.add(buildLinkedInUrl(encodedTitle, encodedLocation));
        
        return urls;
    }
    
    private String buildNaukriUrl(String title, String location) {
        return String.format("https://www.naukri.com/%s-jobs-in-%s", title, location);
    }
    
    private String buildIndeedUrl(String title, String location) {
        return String.format("https://in.indeed.com/jobs?q=%s&l=%s", title, location);
    }
    
    private String buildLinkedInUrl(String title, String location) {
        return String.format("https://www.linkedin.com/jobs/search/?keywords=%s&location=%s", title, location);
    }
    
    private String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return value.replace(" ", "-").toLowerCase();
        }
    }
}
