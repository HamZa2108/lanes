package com.hamzazine.lanes.repository;

import com.hamzazine.lanes.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findBySessionIdOrderByCreatedAtDesc(UUID sessionId);

    Optional<Board> findByIdAndSessionId(Long id, UUID sessionId);
}