package com.joa.prexixionapi.modules.rus.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.rus.dto.SignerRusRequest;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.rus.repositories.SignerRusRepository;
import com.joa.prexixionapi.modules.rus.dto.SignerRusListDTO;

@Service
public class SignerRusService {

    @Autowired
    SignerRusRepository signerRusRepository;

    public DataTablesResponseDTO<SignerRusListDTO> list(SignerRusRequest req) {
        List<SignerRusListDTO> data = signerRusRepository.list(req);
        List<IndicadorDTO> indicadores = signerRusRepository.getSummaryEstados(req);
        int total = indicadores.stream().mapToInt(IndicadorDTO::getCantidad).sum();
        indicadores.add(new IndicadorDTO("TOTAL", total));

        return DataTablesResponseDTO.<SignerRusListDTO>builder()
                .data(data)
                .indicadores(indicadores)
                .build();
    }
}
