package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController {

    @PreAuthorize("hasAuthority('CLIENTES_VER')")
    @GetMapping
    public List<String> obtenerClientes() {
        return List.of("Cliente A", "Cliente B", "Cliente C");
    }

    @PreAuthorize("hasAuthority('CLIENTES_CREAR')")
    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody String nombre) {
        return ResponseEntity.ok("Cliente creado: " + nombre);
    }

}
