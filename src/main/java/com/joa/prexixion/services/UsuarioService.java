package com.joa.prexixion.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.entities.Permiso;
import com.joa.prexixion.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean validarCredenciales(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .map(u -> u.getPassword().equals(password)) // luego cambiar por BCrypt
                .orElse(false);
    }

    public List<String> obtenerPermisos(String username) {
        return usuarioRepository.findByUsername(username)
                .map(u -> u.getPermisos().stream()
                        .map(Permiso::getCodigo)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
