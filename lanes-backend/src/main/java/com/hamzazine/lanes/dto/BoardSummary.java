package com.hamzazine.lanes.dto;

import java.time.Instant;

public record BoardSummary(
        Long id,
        String name,
        Instant createdAt
) {}