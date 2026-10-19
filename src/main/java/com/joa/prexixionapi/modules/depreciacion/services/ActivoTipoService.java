package com.joa.prexixionapi.modules.depreciacion.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoTipoDTO;
import com.joa.prexixionapi.modules.depreciacion.services.ActivoTipoService;
import com.joa.prexixionapi.modules.depreciacion.repositories.ActivoTipoRepository;

@Service
@RequiredArgsConstructor
public class ActivoTipoService {

    private final ActivoTipoRepository repository;

    public List<ActivoTipoDTO> list() {
        return repository.list();
    }

    public ActivoTipoDTO getById(String id) {
        return repository.getById(id);
    }

    public void saveActivoTipo(ActivoTipoDTO dto) {
        repository.saveActivoTipo(dto.getId(), dto.getDescripcion(), 
                                dto.getDepreciacionContable(), dto.getDepreciacionTributaria());
    }
}
