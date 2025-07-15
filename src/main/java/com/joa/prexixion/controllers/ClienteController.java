package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.entities.Cliente;
import com.joa.prexixion.services.ClienteService;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {
    
    @Autowired
    ClienteService clienteService;

    @GetMapping
    public List<Cliente> list() {
        return clienteService.list();
    }
}
