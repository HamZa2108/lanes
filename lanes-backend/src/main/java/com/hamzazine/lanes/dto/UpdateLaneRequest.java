package com.hamzazine.lanes.dto;

import jakarta.validation.constraints.Size;

public record UpdateLaneRequest(
        @Size(max = 120) String name,
        Integer position
) {}