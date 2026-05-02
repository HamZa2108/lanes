package com.hamzazine.lanes.dto;

import java.time.Instant;

public record CardDetail(
        Long id,
        String title,
        String description,
        int position,
        Instant createdAt
) {}