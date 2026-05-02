package com.hamzazine.lanes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLaneRequest(
        @NotBlank @Size(max = 120) String name
) {}