package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.repositories.ClienteRepository;
import com.joa.prexixionapi.dto.ClienteDataTablesRequest;
import com.joa.prexixionapi.dto.ClienteDataTablesResponse;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    
    public List<Cliente> list() {
        return clienteRepository.list();
    }

    public ClienteDataTablesResponse listServerSide(ClienteDataTablesRequest req) {
        ClienteDataTablesResponse response = new ClienteDataTablesResponse();
        response.setDraw(req.getDraw());
        
        int total = clienteRepository.countServerSide(req);
        response.setRecordsTotal(total);
        response.setRecordsFiltered(total);
        
        response.setData(clienteRepository.listServerSide(req));
        response.setSummaryEstados(clienteRepository.getSummaryEstadosServerSide(req));
        response.setSummaryCategorias(clienteRepository.getSummaryCategoriasServerSide(req));
        
        return response;
    }
}
