package com.joa.prexixionapi.common.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
