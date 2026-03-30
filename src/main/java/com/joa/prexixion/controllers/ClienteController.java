package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.entities.Cliente;
import com.joa.prexixion.services.ClienteService;
import com.joa.prexixion.dto.ClienteDataTablesRequest;
import com.joa.prexixion.dto.ClienteDataTablesResponse;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {
    
    @Autowired
    ClienteService clienteService;

    @GetMapping
    public List<Cliente> list() {
        return clienteService.list();
    }

    @GetMapping("/server-side")
    public ClienteDataTablesResponse listServerSide(ClienteDataTablesRequest req) {
        return clienteService.listServerSide(req);
    }
}
