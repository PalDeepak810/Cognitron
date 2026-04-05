package com.proc.proc.Service;

import com.proc.proc.Model.CrawlQuotaConsumption;
import com.proc.proc.Model.CrawlRunProgress;
import com.proc.proc.Repository.CrawlQuotaConsumptionRepo;
import com.proc.proc.Repository.CrawlRunProgressRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CrawlQuotaService {

    private final CrawlRunProgressRepo crawlRunProgressRepo;
    private final CrawlQuotaConsumptionRepo crawlQuotaConsumptionRepo;

    @Value("${cognitron.crawl.global-max-pages-per-24h:200}")
    private int globalMaxPagesPer24h;

    public CrawlQuotaService(
            CrawlRunProgressRepo crawlRunProgressRepo,
            CrawlQuotaConsumptionRepo crawlQuotaConsumptionRepo
    ) {
        this.crawlRunProgressRepo = crawlRunProgressRepo;
        this.crawlQuotaConsumptionRepo = crawlQuotaConsumptionRepo;
    }

    public long remainingGlobalBudget() {
        if (globalMaxPagesPer24h <= 0) {
            return Long.MAX_VALUE;
        }
        long usedLast24h = usedGlobalBudgetLast24h();
        return Math.max(0, globalMaxPagesPer24h - usedLast24h);
    }

    public long usedGlobalBudgetLast24h() {
        return crawlQuotaConsumptionRepo.countByConsumedAtAfter(LocalDateTime.now().minusHours(24));
    }

    public int globalMaxPagesPer24h() {
        return globalMaxPagesPer24h;
    }

    @Transactional
    public boolean tryAcquireRunSlot(String runId, Integer runLimitFromMessage) {
        if (runId == null || runId.isBlank()) {
            crawlQuotaConsumptionRepo.save(new CrawlQuotaConsumption("LEGACY"));
            return true;
        }

        int requestedLimit = runLimitFromMessage != null && runLimitFromMessage > 0
                ? runLimitFromMessage
                : Integer.MAX_VALUE;

        CrawlRunProgress progress = crawlRunProgressRepo.findById(runId).orElse(null);
        if (progress == null) {
            progress = new CrawlRunProgress();
            progress.setRunId(runId);
            progress.setRunLimit(requestedLimit);
            progress.setProcessedCount(0);
        }

        int effectiveLimit = progress.getRunLimit() != null && progress.getRunLimit() > 0
                ? progress.getRunLimit()
                : requestedLimit;

        if (progress.getProcessedCount() >= effectiveLimit) {
            return false;
        }

        progress.setRunLimit(effectiveLimit);
        progress.setProcessedCount(progress.getProcessedCount() + 1);
        crawlRunProgressRepo.save(progress);
        crawlQuotaConsumptionRepo.save(new CrawlQuotaConsumption(runId));
        return true;
    }

    public long remainingRunBudget(String runId, Integer runLimitFromMessage) {
        if (runId == null || runId.isBlank()) {
            return Long.MAX_VALUE;
        }

        int requestedLimit = runLimitFromMessage != null && runLimitFromMessage > 0
                ? runLimitFromMessage
                : Integer.MAX_VALUE;

        CrawlRunProgress progress = crawlRunProgressRepo.findById(runId).orElse(null);
        if (progress == null) {
            return requestedLimit;
        }

        int effectiveLimit = progress.getRunLimit() != null && progress.getRunLimit() > 0
                ? progress.getRunLimit()
                : requestedLimit;

        return Math.max(0, effectiveLimit - progress.getProcessedCount());
    }
}
