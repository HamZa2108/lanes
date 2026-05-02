package com.hamzazine.lanes.service;

import com.hamzazine.lanes.dto.CreateLaneRequest;
import com.hamzazine.lanes.dto.LaneDetail;
import com.hamzazine.lanes.dto.UpdateLaneRequest;
import com.hamzazine.lanes.entity.Board;
import com.hamzazine.lanes.entity.Lane;
import com.hamzazine.lanes.exception.NotFoundException;
import com.hamzazine.lanes.repository.BoardRepository;
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
public class LaneService {

    private final LaneRepository laneRepository;
    private final BoardRepository boardRepository;
    private final SessionContext sessionContext;

    @Transactional
    public LaneDetail create(Long boardId, CreateLaneRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Board board = boardRepository.findByIdAndSessionId(boardId, sessionId)
                .orElseThrow(() -> new NotFoundException("Board not found"));
        int nextPosition = laneRepository.findMaxPositionByBoardId(boardId) + 1;
        Lane lane = Lane.builder()
                .board(board)
                .name(request.name().trim())
                .position(nextPosition)
                .build();
        lane = laneRepository.save(lane);
        return EntityMapper.toLaneDetail(lane);
    }

    @Transactional
    public LaneDetail update(Long id, UpdateLaneRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Lane lane = laneRepository.findByIdAndBoardSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Lane not found"));

        if (request.name() != null && !request.name().isBlank()) {
            lane.setName(request.name().trim());
        }

        if (request.position() != null) {
            reorderLane(lane, request.position());
        }

        return EntityMapper.toLaneDetail(lane);
    }

    @Transactional
    public void delete(Long id) {
        UUID sessionId = sessionContext.getSessionId();
        Lane lane = laneRepository.findByIdAndBoardSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Lane not found"));
        laneRepository.delete(lane);
    }

    private void reorderLane(Lane lane, int newPosition) {
        Long boardId = lane.getBoard().getId();
        List<Lane> siblings = new ArrayList<>(laneRepository.findByBoardIdOrderByPositionAsc(boardId));
        siblings.removeIf(l -> l.getId().equals(lane.getId()));
        int clamped = Math.min(Math.max(newPosition, 0), siblings.size());
        siblings.add(clamped, lane);
        for (int i = 0; i < siblings.size(); i++) {
            siblings.get(i).setPosition(i);
        }
    }
}