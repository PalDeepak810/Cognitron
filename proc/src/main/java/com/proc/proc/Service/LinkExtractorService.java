package com.proc.proc.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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


    public Set<String> filterLinks(Set<String> rawLinks) {
        if (rawLinks == null || rawLinks.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> filtered = new HashSet<>();

        rawLinks.forEach(url -> {
            if (url.isBlank()) return;


            if (url.startsWith("javascript:") || url.startsWith("mailto:") || url.startsWith("tel:")) return;

            if (!url.startsWith("http")) return;

            url = removeFragment(url);
            url = removeTrailingSlash(url);

            if (hasBlockedExtension(url)) return;

            filtered.add(url);
        });

        return filtered;
    }

    private String removeFragment(String url) {
        int idx = url.indexOf("#");
        return (idx != -1) ? url.substring(0, idx) : url;
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
}
