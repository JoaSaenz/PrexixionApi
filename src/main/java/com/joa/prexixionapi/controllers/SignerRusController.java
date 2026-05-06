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

    /** Listado completo (sin paginación). */
    @GetMapping
    public List<Cliente> list() {
        try {
            return signerRusService.list();
        } catch (Exception e) {
            log.error("Error al listar Signers RUS", e);
            throw e;
        }
    }

    /** Server-side: paginado + filtros + resumen de estados. */
    @GetMapping("/server-side")
    public SignerRusResponse listServerSide(SignerRusRequest req) {
        try {
            return signerRusService.listServerSide(req);
        } catch (Exception e) {
            log.error("Error en server-side SignerRus para req: {}", req, e);
            throw e;
        }
    }
}
