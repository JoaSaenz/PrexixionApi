package com.joa.prexixionapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.dto.ReunionDataTablesResponse;
import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.repositories.ReunionRepository;
import com.joa.prexixionapi.dto.ReunionAreaDTO;
import com.joa.prexixionapi.dto.ReunionAcuerdoDTO;

@Service
public class ReunionService {

    @Autowired
    private ReunionRepository reunionRepository;

    public ReunionDataTablesResponse listServerSide(ReunionDataTablesRequest req) {
        List<ReunionDTO> data = reunionRepository.listServerSide(req);
        
        if (!data.isEmpty()) {
            List<Integer> ids = data.stream().map(ReunionDTO::getId).collect(Collectors.toList());
            
            // Batch fetch related data
            var temas = reunionRepository.fetchTemas(ids).stream().collect(Collectors.groupingBy(t -> t.getIdReunion()));
            var partExt = reunionRepository.fetchParticipantesExternos(ids).stream().collect(Collectors.groupingBy(p -> p.getIdReunion()));
            var partInt = reunionRepository.fetchParticipantesInternos(ids).stream().collect(Collectors.groupingBy(p -> p.getIdReunion()));
            var areasMap = reunionRepository.fetchAreas(ids).stream().collect(Collectors.groupingBy(ReunionAreaDTO::getIdReunion));
            var acuerdosMap = reunionRepository.fetchAcuerdos(ids).stream().collect(Collectors.groupingBy(ReunionAcuerdoDTO::getIdReunion));
            
            // Assign sub-items to DTOs
            data.forEach(r -> {
                r.setTemas(temas.getOrDefault(r.getId(), new ArrayList<>()));
                r.setParticipantesExternos(partExt.getOrDefault(r.getId(), new ArrayList<>()));
                r.setParticipantesInternos(partInt.getOrDefault(r.getId(), new ArrayList<>()));
                
                if (areasMap.containsKey(r.getId())) {
                    r.setAreas(areasMap.get(r.getId()).stream()
                            .map(m -> new Gclass(m.getId(), m.getDescripcion()))
                            .collect(Collectors.toList()));
                } else {
                    r.setAreas(new ArrayList<>());
                }
                
                if (acuerdosMap.containsKey(r.getId())) {
                    r.setAcuerdos(acuerdosMap.get(r.getId()).stream()
                            .map(m -> new Gclass(m.getId(), m.getAcuerdo()))
                            .collect(Collectors.toList()));
                } else {
                    r.setAcuerdos(new ArrayList<>());
                }
            });
        }
        
        long filtered = reunionRepository.countServerSide(req);
        long total = reunionRepository.countTotal();

        return ReunionDataTablesResponse.builder()
                .draw(req.getDraw())
                .recordsTotal(total)
                .recordsFiltered(filtered)
                .data(data)
                .build();
    }
}
