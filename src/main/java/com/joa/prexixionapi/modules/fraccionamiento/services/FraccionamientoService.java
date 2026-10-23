package com.joa.prexixionapi.modules.fraccionamiento.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoFilterRequestDTO;
import com.joa.prexixionapi.modules.fraccionamiento.dto.FraccionamientoListDTO;
import com.joa.prexixionapi.modules.fraccionamiento.repositories.FraccionamientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraccionamientoService {

    private final FraccionamientoRepository fraccionamientoRepository;

    public DataTablesResponseDTO<FraccionamientoListDTO> list(FraccionamientoFilterRequestDTO req) {
        List<FraccionamientoListDTO> list = fraccionamientoRepository.list(req);
        return DataTablesResponseDTO.<FraccionamientoListDTO>builder()
                .data(list)
                .build();
    }
}
