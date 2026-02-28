package com.proc.proc.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visited_urls")
public class VisitedUrl {

    @Id
    @Column(length = 64, nullable = false)
    private String urlHash;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    public VisitedUrl() {}

    public VisitedUrl(String url) {
        this.url = url;
        this.urlHash = sha256(url);
    }

    public VisitedUrl(String url, String urlHash) {
        this.url = url;
        this.urlHash = urlHash;
    }

    public String getUrlHash() {
        return urlHash;
    }

    public String getUrl() {
        return url;
    }

    public static String sha256(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
