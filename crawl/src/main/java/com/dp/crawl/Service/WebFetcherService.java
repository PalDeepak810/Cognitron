package com.dp.crawl.Service;

import com.dp.crawl.Model.CrawlMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class WebFetcherService {

    public CrawlMessage fetch(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Cognitron-Crawler/1.0 (+https://your-site.example/contact)")
                    .timeout(15000)
                    .get();

            String title = doc.title();
            String bodyText = doc.body() != null ? doc.body().text() : "";
            String html = doc.html();

            CrawlMessage message = new CrawlMessage();
            message.setUrl(url);
            message.setTitle(title);
            message.setText(bodyText);
            message.setHtml(html);
            message.setCrawledAt(OffsetDateTime.now());
            message.setSource(getDomain(url));

            return message;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch URL: " + url, e);
        }
    }

    private String getDomain(String url) {
        try {
            String domain = new java.net.URI(url).getHost();
            if (domain == null) return "unknown";
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
