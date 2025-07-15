package com.joa.prexixion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.entities.Cliente;
import com.joa.prexixion.repositories.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    
    public List<Cliente> list() {
        return clienteRepository.list();
    }
}
