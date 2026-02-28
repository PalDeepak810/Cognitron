package com.proc.proc.Service;

import com.proc.proc.Model.CrawlConfig;
import com.proc.proc.Model.CrawlMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class LinkExtractorService {

    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
            ".css", ".js", ".jpg", ".jpeg", ".png", ".svg", ".gif",
            ".ico", ".pdf", ".zip", ".rar", ".mp4", ".mp3", ".woff",
            ".ttf", ".eot", ".exe", ".bin"
    );

    public Set<String> extractLinks(String html) {
        if (html == null || html.isBlank()) {
            return Collections.emptySet();
        }

        Set<String> links = new HashSet<>();
        Document doc = Jsoup.parse(html);

        doc.select("a[href]").forEach(element -> {
            String url = element.attr("abs:href").trim();
            if (!url.isEmpty()) {
                links.add(url);
            }
        });

        return links;
    }

    public Set<String> filterLinks(Set<String> raw, CrawlMessage msg) {
        CrawlConfig config = msg.getConfig();
        if (config == null) {
            return Set.of();
        }

        int depth = msg.getDepth();

        if (depth >= config.getMaxDepth()) return Set.of();

        return raw.stream()
                .filter(url -> !hasBlockedExtension(url))
                .filter(this::isValidUrl)
                .filter(url -> config.isRestrictDomain() ? sameDomain(url, msg.getUrl()) : true)
                .filter(url -> matchesKeywords(url, config.getTopicKeywords()))
                .collect(Collectors.toSet());
    }

    private boolean matchesKeywords(String url, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return true;
        }

        String lowerUrl = url.toLowerCase();
        return keywords.stream().anyMatch(keyword ->
                lowerUrl.contains(keyword.toLowerCase())
        );
    }

    private boolean sameDomain(String url, String baseUrl) {
        try {
            String domain1 = new java.net.URL(url).getHost();
            String domain2 = new java.net.URL(baseUrl).getHost();

            if (domain1 == null || domain2 == null) {
                return false;
            }

            domain1 = domain1.replaceAll("^www\\.", "");
            domain2 = domain2.replaceAll("^www\\.", "");

            return domain1.equals(domain2);
        } catch (Exception e) {
            return false;
        }
    }

    private String removeFragment(String url) {
        int idx = url.indexOf("#");
        return (idx != -1) ? url.substring(0, idx) : url;
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private boolean hasBlockedExtension(String url) {
        String lower = url.toLowerCase();
        return BLOCKED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    public boolean isRelevant(CrawlMessage msg) {

        if (msg.getConfig() == null ||
                msg.getConfig().getTopicKeywords() == null ||
                msg.getConfig().getTopicKeywords().isEmpty()) {
            return true;
        }

        String text = (msg.getTitle() + " " + msg.getText()).toLowerCase();

        for (String keyword : msg.getConfig().getTopicKeywords()) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
