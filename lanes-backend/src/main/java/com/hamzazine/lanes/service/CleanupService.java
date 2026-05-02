package com.hamzazine.lanes.service;

import com.hamzazine.lanes.entity.Session;
import com.hamzazine.lanes.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final SessionRepository sessionRepository;

    @Value("${app.session.cleanup-after-days:30}")
    private int cleanupAfterDays;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldSessions() {
        Instant cutoff = Instant.now().minus(cleanupAfterDays, ChronoUnit.DAYS);
        List<Session> stale = sessionRepository.findByLastSeenAtBefore(cutoff);
        if (stale.isEmpty()) {
            log.info("Session cleanup: no stale sessions");
            return;
        }
        sessionRepository.deleteAll(stale);
        log.info("Session cleanup: deleted {} sessions inactive since {}", stale.size(), cutoff);
    }
}