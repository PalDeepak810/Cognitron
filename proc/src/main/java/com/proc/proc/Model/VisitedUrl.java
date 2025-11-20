package com.proc.proc.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visited_urls")
public class VisitedUrl {

    @Id
    private String url;

    public VisitedUrl() {}

    public VisitedUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}