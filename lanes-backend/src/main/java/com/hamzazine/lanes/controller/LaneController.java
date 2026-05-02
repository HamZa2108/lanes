package com.hamzazine.lanes.controller;

import com.hamzazine.lanes.dto.CardDetail;
import com.hamzazine.lanes.dto.CreateCardRequest;
import com.hamzazine.lanes.dto.LaneDetail;
import com.hamzazine.lanes.dto.UpdateLaneRequest;
import com.hamzazine.lanes.service.CardService;
import com.hamzazine.lanes.service.LaneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lanes")
@RequiredArgsConstructor
public class LaneController {

    private final LaneService laneService;
    private final CardService cardService;

    @PatchMapping("/{id}")
    public LaneDetail update(@PathVariable Long id,
                             @Valid @RequestBody UpdateLaneRequest request) {
        return laneService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        laneService.delete(id);
    }

    @PostMapping("/{laneId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardDetail createCard(@PathVariable Long laneId,
                                 @Valid @RequestBody CreateCardRequest request) {
        return cardService.create(laneId, request);
    }
}