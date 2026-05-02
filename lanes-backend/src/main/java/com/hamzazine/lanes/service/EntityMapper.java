package com.hamzazine.lanes.service;

import com.hamzazine.lanes.dto.BoardDetail;
import com.hamzazine.lanes.dto.BoardSummary;
import com.hamzazine.lanes.dto.CardDetail;
import com.hamzazine.lanes.dto.LaneDetail;
import com.hamzazine.lanes.entity.Board;
import com.hamzazine.lanes.entity.Card;
import com.hamzazine.lanes.entity.Lane;

public final class EntityMapper {

    private EntityMapper() {}

    public static BoardSummary toSummary(Board board) {
        return new BoardSummary(board.getId(), board.getName(), board.getCreatedAt());
    }

    public static BoardDetail toDetail(Board board) {
        return new BoardDetail(
                board.getId(),
                board.getName(),
                board.getCreatedAt(),
                board.getLanes().stream().map(EntityMapper::toLaneDetail).toList()
        );
    }

    public static LaneDetail toLaneDetail(Lane lane) {
        return new LaneDetail(
                lane.getId(),
                lane.getName(),
                lane.getPosition(),
                lane.getCards().stream().map(EntityMapper::toCardDetail).toList()
        );
    }

    public static CardDetail toCardDetail(Card card) {
        return new CardDetail(
                card.getId(),
                card.getTitle(),
                card.getDescription(),
                card.getPosition(),
                card.getCreatedAt()
        );
    }
}