package com.hamzazine.lanes.service;

import com.hamzazine.lanes.dto.BoardDetail;
import com.hamzazine.lanes.dto.BoardSummary;
import com.hamzazine.lanes.dto.CreateBoardRequest;
import com.hamzazine.lanes.dto.UpdateBoardRequest;
import com.hamzazine.lanes.entity.Board;
import com.hamzazine.lanes.entity.Session;
import com.hamzazine.lanes.exception.NotFoundException;
import com.hamzazine.lanes.repository.BoardRepository;
import com.hamzazine.lanes.repository.SessionRepository;
import com.hamzazine.lanes.session.SessionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final SessionRepository sessionRepository;
    private final SessionContext sessionContext;

    @Transactional(readOnly = true)
    public List<BoardSummary> list() {
        UUID sessionId = sessionContext.getSessionId();
        return boardRepository.findBySessionIdOrderByCreatedAtDesc(sessionId).stream()
                .map(EntityMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardDetail get(Long id) {
        UUID sessionId = sessionContext.getSessionId();
        Board board = boardRepository.findByIdAndSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Board not found"));
        return EntityMapper.toDetail(board);
    }

    @Transactional
    public BoardSummary create(CreateBoardRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found"));
        Board board = Board.builder()
                .session(session)
                .name(request.name().trim())
                .build();
        board = boardRepository.save(board);
        return EntityMapper.toSummary(board);
    }

    @Transactional
    public BoardSummary update(Long id, UpdateBoardRequest request) {
        UUID sessionId = sessionContext.getSessionId();
        Board board = boardRepository.findByIdAndSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Board not found"));
        board.setName(request.name().trim());
        return EntityMapper.toSummary(board);
    }

    @Transactional
    public void delete(Long id) {
        UUID sessionId = sessionContext.getSessionId();
        Board board = boardRepository.findByIdAndSessionId(id, sessionId)
                .orElseThrow(() -> new NotFoundException("Board not found"));
        boardRepository.delete(board);
    }
}