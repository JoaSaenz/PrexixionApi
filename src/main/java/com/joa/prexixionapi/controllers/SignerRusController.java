package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.dto.SignerRusRequest;
import com.joa.prexixionapi.dto.SignerRusResponse;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.services.SignerRusService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/signer-rus")
@Slf4j
public class SignerRusController {

    @Autowired
    SignerRusService signerRusService;

    @GetMapping
    public SignerRusResponse list(SignerRusRequest req) {
        try {
            return signerRusService.list(req);
        } catch (Exception e) {
            log.error("Error al listar Signers RUS", e);
            throw e;
        }
    }
}
