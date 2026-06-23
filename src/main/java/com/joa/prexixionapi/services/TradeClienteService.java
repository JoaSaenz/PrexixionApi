package com.joa.prexixionapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.TradeClienteDataTablesRequest;
import com.joa.prexixionapi.dto.TradeClienteDataTablesResponse;
import com.joa.prexixionapi.repositories.TradeClienteRepository;

@Service
public class TradeClienteService {

    @Autowired
    private TradeClienteRepository tradeClienteRepository;

    public TradeClienteDataTablesResponse listServerSide(TradeClienteDataTablesRequest req) {
        TradeClienteDataTablesResponse response = new TradeClienteDataTablesResponse();
        response.setData(tradeClienteRepository.listServerSide(req));
        response.setSummaryEstados(tradeClienteRepository.getSummaryEstadosServerSide(req));
        return response;
    }
}
