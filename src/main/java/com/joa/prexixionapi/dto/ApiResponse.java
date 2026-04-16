package com.joa.prexixionapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructor para compatibilidad con formato antiguo (status, message, timestamp)
    public ApiResponse(String status, String message, LocalDateTime timestamp) {
        this.success = "OK".equals(status);
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Constructor para formato nuevo (success, message, data)
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Método para compatibilidad con código que usa .getStatus()
    public String getStatus() {
        return success ? "OK" : "ERROR";
    }
}
