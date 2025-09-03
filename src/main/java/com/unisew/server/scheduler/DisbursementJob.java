package com.unisew.server.scheduler;

import com.unisew.server.services.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DisbursementJob {

    private final DisbursementService disbursementService;

    @Scheduled(cron = "0 0/5 2 * * *", zone = "Asia/Ho_Chi_Minh")
    public void run() {
        disbursementService.disburseDue(Instant.now());
    }
}
