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

    /** Listado completo (sin paginación). */
    public List<Cliente> list() {
        return signerRusRepository.list();
    }

    /** Server-side: paginado, filtrado y con resumen de estados. */
    public SignerRusResponse listServerSide(SignerRusRequest req) {
        SignerRusResponse response = new SignerRusResponse();
        response.setDraw(req.getDraw());

        int total = signerRusRepository.countServerSide(req);
        response.setRecordsTotal(total);
        response.setRecordsFiltered(total);

        response.setData(signerRusRepository.listServerSide(req));
        response.setSummaryEstados(signerRusRepository.getSummaryEstados(req));

        return response;
    }
}
