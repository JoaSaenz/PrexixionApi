package com.joa.prexixionapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.dto.ReunionListDTO;
import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.dto.ReunionDataTablesResponse;
import com.joa.prexixionapi.repositories.ReunionRepository;

@Service
public class ReunionService {

    @Autowired
    private ReunionRepository reunionRepository;

    public ReunionDataTablesResponse list(ReunionDataTablesRequest req) {
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

        return ReunionDataTablesResponse.builder()
                .data(data)
                .summaryEstados(reunionRepository.getSummaryEstados(req))
                .build();
    }

    public ReunionDTO getById(int idReunion) {
        return reunionRepository.getById(idReunion);
    }

    public ReunionDTO save(ReunionDTO reunion) {
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
