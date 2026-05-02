package com.hamzazine.lanes.dto;

import java.util.List;

public record LaneDetail(
        Long id,
        String name,
        int position,
        List<CardDetail> cards
) {}