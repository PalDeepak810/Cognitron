package com.dp.crawl.Service;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobSiteRegistryTest {

    private final JobSiteRegistry registry = new JobSiteRegistry();

    @Test
    void testBuildSearchUrls_BasicInput() {
        List<String> urls = registry.buildSearchUrls("Software Engineer", "Bangalore");
        
        assertEquals(3, urls.size());
        assertTrue(urls.get(0).contains("naukri.com"));
        assertTrue(urls.get(1).contains("indeed.com"));
        assertTrue(urls.get(2).contains("linkedin.com"));
        
        System.out.println("✅ Test 1: Basic Input");
        urls.forEach(System.out::println);
    }

    @Test
    void testBuildSearchUrls_WithSpaces() {
        List<String> urls = registry.buildSearchUrls("Data Scientist", "New Delhi");
        
        assertTrue(urls.get(0).contains("Data%20Scientist"));
        assertTrue(urls.get(1).contains("Data%20Scientist"));
        assertTrue(urls.get(2).contains("Data%20Scientist"));
        
        System.out.println("\n✅ Test 2: With Spaces");
        urls.forEach(System.out::println);
    }

    @Test
    void testBuildSearchUrls_SpecialCharacters() {
        List<String> urls = registry.buildSearchUrls("Java/J2EE Developer", "Mumbai");
        
        assertFalse(urls.isEmpty());
        
        System.out.println("\n✅ Test 3: Special Characters");
        urls.forEach(System.out::println);
    }

    @Test
    void testUrlEncoding() {
        List<String> urls = registry.buildSearchUrls("Full Stack Developer", "Hyderabad");
        
        urls.forEach(url -> {
            assertFalse(url.contains(" "), "URL should not contain spaces");
        });
        
        System.out.println("\n✅ Test 4: URL Encoding");
        urls.forEach(System.out::println);
    }
}
