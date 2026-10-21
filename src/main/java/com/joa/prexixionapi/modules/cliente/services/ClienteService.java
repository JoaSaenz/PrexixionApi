package com.joa.prexixionapi.modules.cliente.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.cliente.services.ClienteService;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesRequest;
import com.joa.prexixionapi.modules.cliente.repositories.ClienteRepository;
import com.joa.prexixionapi.modules.cliente.dto.ClienteDataTablesResponse;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    public ClienteDataTablesResponse list(ClienteDataTablesRequest req) {
        ClienteDataTablesResponse response = new ClienteDataTablesResponse();
        response.setData(clienteRepository.list(req));
        response.setSummaryEstados(clienteRepository.getSummaryEstados(req));
        response.setSummaryCategorias(clienteRepository.getSummaryCategorias(req));

        return response;
    }

    public List<java.util.Map<String, Object>> getEmpresasSelector() {
        return clienteRepository.getEmpresasSelector();
    }
}
