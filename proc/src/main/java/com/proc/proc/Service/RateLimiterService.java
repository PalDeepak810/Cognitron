package com.proc.proc.Service;


import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final ConcurrentHashMap<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();

    public void acquire(String domain) {
        limiterMap.computeIfAbsent(domain, d -> RateLimiter.create(1.0)).acquire();
    }
}
