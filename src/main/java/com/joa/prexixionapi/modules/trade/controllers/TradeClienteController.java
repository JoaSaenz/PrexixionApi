package com.joa.prexixionapi.modules.trade.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.joa.prexixionapi.modules.trade.dto.TradeClienteDataTablesRequest;
import com.joa.prexixionapi.modules.trade.dto.TradeClienteDataTablesResponse;
import com.joa.prexixionapi.modules.trade.services.TradeClienteService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trade-cliente")
@Slf4j
public class TradeClienteController {

    private final TradeClienteService tradeClienteService;

    @GetMapping
    public ResponseEntity<TradeClienteDataTablesResponse> list(TradeClienteDataTablesRequest req) throws Exception {
        return ResponseEntity.ok(tradeClienteService.list(req));
    }
}
