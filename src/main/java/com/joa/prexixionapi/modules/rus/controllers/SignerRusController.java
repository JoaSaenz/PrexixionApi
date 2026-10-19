package com.joa.prexixionapi.modules.rus.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.joa.prexixionapi.modules.rus.dto.SignerRusRequest;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.rus.services.SignerRusService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import com.joa.prexixionapi.modules.rus.dto.SignerRusListDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signer-rus")
@Slf4j
public class SignerRusController {

    private final SignerRusService signerRusService;

    @PostMapping("/list")
    public ResponseEntity<DataTablesResponseDTO<SignerRusListDTO>> list(@RequestBody SignerRusRequest req) {
        return ResponseEntity.ok(signerRusService.list(req));
    }
}
