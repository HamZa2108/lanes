package com.hamzazine.lanes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 5000) String description
) {}