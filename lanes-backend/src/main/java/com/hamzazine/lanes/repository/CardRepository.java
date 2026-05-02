package com.hamzazine.lanes.repository;

import com.hamzazine.lanes.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByIdAndLaneBoardSessionId(Long id, UUID sessionId);

    List<Card> findByLaneIdOrderByPositionAsc(Long laneId);

    @Query("SELECT COALESCE(MAX(c.position), -1) FROM Card c WHERE c.lane.id = :laneId")
    int findMaxPositionByLaneId(Long laneId);
}