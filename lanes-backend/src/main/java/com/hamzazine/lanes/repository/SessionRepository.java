package com.hamzazine.lanes.repository;

import com.hamzazine.lanes.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByLastSeenAtBefore(Instant cutoff);
}