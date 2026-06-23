package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.AuthRequest;
import com.joa.prexixionapi.dto.AuthResponse;
import com.joa.prexixionapi.repositories.UserRepository;
import com.joa.prexixionapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getDni(),
                            request.getClave()
                    )
            );
            
            var user = userRepository.findById(request.getDni())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    
            var jwtToken = jwtService.generateToken(user);
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .dni(user.getDni())
                    .success(true)
                    .message("Autenticación exitosa")
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Credenciales incorrectas o usuario no habilitado: " + e.getMessage())
                    .build();
        }
    }
}
