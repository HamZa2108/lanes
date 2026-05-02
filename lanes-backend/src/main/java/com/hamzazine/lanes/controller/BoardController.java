package com.hamzazine.lanes.controller;

import com.hamzazine.lanes.dto.BoardDetail;
import com.hamzazine.lanes.dto.BoardSummary;
import com.hamzazine.lanes.dto.CreateBoardRequest;
import com.hamzazine.lanes.dto.CreateLaneRequest;
import com.hamzazine.lanes.dto.LaneDetail;
import com.hamzazine.lanes.dto.UpdateBoardRequest;
import com.hamzazine.lanes.service.BoardService;
import com.hamzazine.lanes.service.LaneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final LaneService laneService;

    @GetMapping
    public List<BoardSummary> list() {
        return boardService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardSummary create(@Valid @RequestBody CreateBoardRequest request) {
        return boardService.create(request);
    }

    @GetMapping("/{id}")
    public BoardDetail get(@PathVariable Long id) {
        return boardService.get(id);
    }

    @PatchMapping("/{id}")
    public BoardSummary update(@PathVariable Long id,
                               @Valid @RequestBody UpdateBoardRequest request) {
        return boardService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }

    @PostMapping("/{boardId}/lanes")
    @ResponseStatus(HttpStatus.CREATED)
    public LaneDetail createLane(@PathVariable Long boardId,
                                 @Valid @RequestBody CreateLaneRequest request) {
        return laneService.create(boardId, request);
    }
}