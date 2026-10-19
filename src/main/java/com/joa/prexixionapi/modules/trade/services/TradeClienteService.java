package com.joa.prexixionapi.modules.trade.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.trade.dto.TradeClienteDataTablesRequest;
import com.joa.prexixionapi.modules.trade.dto.TradeClienteDataTablesResponse;
import com.joa.prexixionapi.modules.trade.repositories.TradeClienteRepository;

@Service
public class TradeClienteService {

    @Autowired
    private TradeClienteRepository tradeClienteRepository;

    public TradeClienteDataTablesResponse list(TradeClienteDataTablesRequest req) {
        TradeClienteDataTablesResponse response = new TradeClienteDataTablesResponse();
        response.setData(tradeClienteRepository.list(req));
        response.setSummaryEstados(tradeClienteRepository.getSummaryEstados(req));
        return response;
    }
}
