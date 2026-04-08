package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.ActivoTipoDTO;
import com.joa.prexixionapi.repositories.ActivoTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivoTipoService {

    private final ActivoTipoRepository repository;

    public List<ActivoTipoDTO> getAllActivoTipos() {
        return repository.findAllRaw().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ActivoTipoDTO getActivoTipoById(String id) {
        Object[] row = repository.findByIdRaw(id);
        return row != null ? mapToDTO(row) : null;
    }

    public void saveActivoTipo(ActivoTipoDTO dto) {
        repository.saveActivoTipo(dto.getId(), dto.getDescripcion(), 
                                dto.getDepreciacionContable(), dto.getDepreciacionTributaria());
    }

    private ActivoTipoDTO mapToDTO(Object[] row) {
        return ActivoTipoDTO.builder()
                .id((String) row[0])
                .descripcion((String) row[1])
                .depreciacionContable(toDouble(row[2]))
                .depreciacionTributaria(toDouble(row[3]))
                .build();
    }

    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return 0.0;
    }
}
