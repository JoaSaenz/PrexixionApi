package com.joa.prexixionapi.modules.gestionSide.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.common.dto.IndicadorDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideListDTO;
import com.joa.prexixionapi.modules.gestionSide.dto.GestionSideRequest;
import com.joa.prexixionapi.modules.gestionSide.repositories.GestionSideRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionSideService {

    private final GestionSideRepository gestionSideRepository;

    public DataTablesResponseDTO<GestionSideListDTO> list(GestionSideRequest req) {
        List<GestionSideListDTO> list = gestionSideRepository.list(req);

        int pendiente = 0;
        int proceso = 0;
        int cerrado = 0;

        for (GestionSideListDTO item : list) {
            if (item.getIdEstado() != null) {
                switch (item.getIdEstado()) {
                    case 1:
                        pendiente++;
                        break;
                    case 2:
                        proceso++;
                        break;
                    case 3:
                        cerrado++;
                        break;
                }
            }
        }

        List<IndicadorDTO> indicadores = new ArrayList<>();
        indicadores.add(new IndicadorDTO("PENDIENTE", pendiente));
        indicadores.add(new IndicadorDTO("PROCESO", proceso));
        indicadores.add(new IndicadorDTO("CERRADO", cerrado));
        indicadores.add(new IndicadorDTO("TOTAL", list.size()));

        DataTablesResponseDTO<GestionSideListDTO> response = new DataTablesResponseDTO<>();
        response.setData(list);
        response.setIndicadores(indicadores);
        return response;
    }

    public GestionSideDTO getOne(int id) {
        return gestionSideRepository.getOne(id);
    }

    @Transactional
    public int insertUpdate(GestionSideDTO dto) {
        return gestionSideRepository.insertUpdate(dto);
    }

    @Transactional
    public void delete(int id) {
        gestionSideRepository.delete(id);
    }
}
