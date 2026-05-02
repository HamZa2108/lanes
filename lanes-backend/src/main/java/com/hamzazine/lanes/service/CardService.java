package com.hamzazine.lanes.service;

import com.hamzazine.lanes.dto.CardDetail;
import com.hamzazine.lanes.dto.CreateCardRequest;
import com.hamzazine.lanes.dto.UpdateCardRequest;
import com.hamzazine.lanes.entity.Card;
import com.hamzazine.lanes.entity.Lane;
import com.hamzazine.lanes.exception.NotFoundException;
import com.hamzazine.lanes.repository.CardRepository;
import com.hamzazine.lanes.repository.LaneRepository;
import com.hamzazine.lanes.session.SessionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final LaneRepository laneRepository;
    private final SessionContext sessionContext;

    @Transactional
    public CardDetail create(Long laneId, CreateCardRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Lane lane = laneRepository.findByIdAndBoardSessionId(laneId, sessionId)
                .orElseThrow(() -> new NotFoundException("Lane not found"));
        int nextPosition = cardRepository.findMaxPositionByLaneId(laneId) + 1;
        Card card = Card.builder()
                .lane(lane)
                .title(request.title().trim())
                .description(request.description())
                .position(nextPosition)
                .build();
        card = cardRepository.save(card);
        return EntityMapper.toCardDetail(card);
    }

    @Transactional
    public CardDetail update(Long id, UpdateCardRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Card card = cardRepository.findByIdAndLaneBoardSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (request.title() != null && !request.title().isBlank()) {
            card.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            card.setDescription(request.description());
        }

        if (request.laneId() != null || request.position() != null) {
            moveCard(card, request.laneId(), request.position(), sessionId);
        }

        return EntityMapper.toCardDetail(card);
    }

    @Transactional
    public void delete(Long id) {
        UUID sessionId = sessionContext.getSessionId();
        Card card = cardRepository.findByIdAndLaneBoardSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        cardRepository.delete(card);
    }

    private void moveCard(Card card, Long targetLaneId, Integer targetPosition, UUID sessionId) {
        Lane sourceLane = card.getLane();
        Lane targetLane;

        if (targetLaneId == null || targetLaneId.equals(sourceLane.getId())) {
            targetLane = sourceLane;
        } else {
            targetLane = laneRepository.findByIdAndBoardSessionId(targetLaneId, sessionId)
                    .orElseThrow(() -> new NotFoundException("Target lane not found"));
            if (!targetLane.getBoard().getId().equals(sourceLane.getBoard().getId())) {
                throw new IllegalArgumentException("Cannot move card across boards");
            }
        }

        boolean sameLane = sourceLane.getId().equals(targetLane.getId());

        List<Card> sourceSiblings = new ArrayList<>(
                cardRepository.findByLaneIdOrderByPositionAsc(sourceLane.getId()));
        sourceSiblings.removeIf(c -> c.getId().equals(card.getId()));

        List<Card> targetSiblings = sameLane
                ? sourceSiblings
                : new ArrayList<>(cardRepository.findByLaneIdOrderByPositionAsc(targetLane.getId()));

        int clamped = targetPosition == null
                ? targetSiblings.size()
                : Math.min(Math.max(targetPosition, 0), targetSiblings.size());

        card.setLane(targetLane);
        targetSiblings.add(clamped, card);

        reindex(targetSiblings);
        if (!sameLane) {
            reindex(sourceSiblings);
        }
    }

    private void reindex(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }
    }
}