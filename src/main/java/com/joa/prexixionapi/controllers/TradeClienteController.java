package com.joa.prexixionapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.dto.TradeClienteDataTablesRequest;
import com.joa.prexixionapi.dto.TradeClienteDataTablesResponse;
import com.joa.prexixionapi.services.TradeClienteService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/trade-cliente")
@Slf4j
public class TradeClienteController {

    @Autowired
    private TradeClienteService tradeClienteService;

    @GetMapping("/server-side")
    public TradeClienteDataTablesResponse listServerSide(TradeClienteDataTablesRequest req) {
        try {
            return tradeClienteService.listServerSide(req);
        } catch (Exception e) {
            log.error("Error en listServerSide para trade clientes con req: {}", req, e);
            throw e;
        }
    }
}
