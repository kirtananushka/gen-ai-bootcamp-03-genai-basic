package com.epam.training.gen.ai.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
