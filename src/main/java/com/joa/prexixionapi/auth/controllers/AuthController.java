package com.joa.prexixionapi.auth.controllers;

import com.joa.prexixionapi.auth.dto.AuthRequest;
import com.joa.prexixionapi.auth.dto.AuthResponse;
import com.joa.prexixionapi.common.dto.ApiResponse;
import com.joa.prexixionapi.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", authService.authenticate(request)));
    }
}
