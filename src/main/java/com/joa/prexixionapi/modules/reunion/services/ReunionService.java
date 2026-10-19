package com.joa.prexixionapi.modules.reunion.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.reunion.dto.ReunionDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionListDTO;
import com.joa.prexixionapi.modules.reunion.dto.ReunionRequest;
import com.joa.prexixionapi.common.dto.DataTablesResponseDTO;
import com.joa.prexixionapi.modules.reunion.repositories.ReunionRepository;
import com.joa.prexixionapi.common.dto.IndicadorDTO;

@Service
public class ReunionService {

    @Autowired
    private ReunionRepository reunionRepository;

    public DataTablesResponseDTO<ReunionListDTO> list(ReunionRequest req) {
        List<ReunionListDTO> data = reunionRepository.list(req);

        if (!data.isEmpty()) {
            List<Integer> ids = data.stream().map(ReunionListDTO::getId).collect(Collectors.toList());

            // Batch fetch related lightweight data
            var temasMap = reunionRepository.fetchTemasStrings(ids);
            var areasMap = reunionRepository.fetchAreasStrings(ids);

            // Assign sub-items to DTOs
            data.forEach(r -> {
                r.setTemas(temasMap.getOrDefault(r.getId(), new ArrayList<>()));
                r.setAreas(areasMap.getOrDefault(r.getId(), new ArrayList<>()));
            });
        }

        List<IndicadorDTO> indicadores = reunionRepository.getSummaryEstados(req);

        int total = indicadores.stream().mapToInt(IndicadorDTO::getCantidad).sum();
        indicadores.add(new IndicadorDTO("Total", total));

        return DataTablesResponseDTO.<ReunionListDTO>builder()
                .data(data)
                .indicadores(indicadores)
                .build();
    }

    public ReunionDTO getOne(int idReunion) {
        return reunionRepository.getOne(idReunion);
    }

    public ReunionDTO insertUpdate(ReunionDTO reunion) {
        if (reunion.getId() == 0) {
            int newId = reunionRepository.insert(reunion);
            reunion.setId(newId);
        } else {
            reunionRepository.update(reunion);
        }
        return reunion;
    }

    public void delete(int idReunion) {
        reunionRepository.delete(idReunion);
    }
}
