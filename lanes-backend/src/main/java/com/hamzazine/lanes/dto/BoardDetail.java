package com.hamzazine.lanes.dto;

import java.time.Instant;
import java.util.List;

public record BoardDetail(
        Long id,
        String name,
        Instant createdAt,
        List<LaneDetail> lanes
) {}