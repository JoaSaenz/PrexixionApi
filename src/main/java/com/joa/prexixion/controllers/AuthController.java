package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.entities.AuthRequest;
import com.joa.prexixion.entities.AuthResponse;
import com.joa.prexixion.services.JwtService;
import com.joa.prexixion.services.UsuarioService;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService; // asumiendo que tú lo tienes

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (!usuarioService.validarCredenciales(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> permisos = usuarioService.obtenerPermisos(request.getUsername());
        String token = jwtService.generateToken(request.getUsername(), permisos);

        return ResponseEntity.ok(new AuthResponse(token, permisos));
    }
}
