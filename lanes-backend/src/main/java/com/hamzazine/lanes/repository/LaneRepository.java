package com.hamzazine.lanes.repository;

import com.hamzazine.lanes.entity.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LaneRepository extends JpaRepository<Lane, Long> {

    Optional<Lane> findByIdAndBoardSessionId(Long id, UUID sessionId);

    List<Lane> findByBoardIdOrderByPositionAsc(Long boardId);

    @Query("SELECT COALESCE(MAX(l.position), -1) FROM Lane l WHERE l.board.id = :boardId")
    int findMaxPositionByBoardId(Long boardId);
}