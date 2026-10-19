package com.joa.prexixionapi.auth.services;

import com.joa.prexixionapi.auth.dto.AuthRequest;
import com.joa.prexixionapi.auth.dto.AuthResponse;
import com.joa.prexixionapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import com.joa.prexixionapi.auth.dto.UserDTO;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthResponse authenticate(AuthRequest request) {
                var authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getDni(),
                                                request.getClave()));

                // Obtenemos el usuario ya validado por Spring Security sin volver a consultar la BD
                var user = (UserDTO) authentication.getPrincipal();

                var jwtToken = jwtService.generateToken(user);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .dni(user.getDni())
                                .build();
        }
}
