package com.hamzazine.lanes.dto;

import jakarta.validation.constraints.Size;

public record UpdateCardRequest(
        @Size(max = 200) String title,
        @Size(max = 5000) String description,
        Long laneId,
        Integer position
) {}