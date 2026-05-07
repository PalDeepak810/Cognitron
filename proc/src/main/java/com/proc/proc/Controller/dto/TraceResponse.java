package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TraceResponse(
        String url,
        List<TraceNode> discoveryPath
) {
    public record TraceNode(
            String url,
            int depth,
            LocalDateTime discoveredAt,
            String parentUrl,
            boolean isSeed
    ) {
    }
}
