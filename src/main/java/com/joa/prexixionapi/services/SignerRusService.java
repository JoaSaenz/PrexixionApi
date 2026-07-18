package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.SignerRusRequest;
import com.joa.prexixionapi.dto.SignerRusResponse;
import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.repositories.SignerRusRepository;

@Service
public class SignerRusService {

    @Autowired
    SignerRusRepository signerRusRepository;

    public SignerRusResponse list(SignerRusRequest req) {
        SignerRusResponse response = new SignerRusResponse();
        response.setData(signerRusRepository.list(req));
        response.setSummaryEstados(signerRusRepository.getSummaryEstados(req));
        return response;
    }
}
