package com.hamzazine.lanes.session;

import com.hamzazine.lanes.entity.Session;
import com.hamzazine.lanes.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public void touchSession(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseGet(() -> {
            Session s = new Session();
            s.setId(sessionId);
            return s;
        });
        session.setLastSeenAt(Instant.now());
        sessionRepository.save(session);
    }
}