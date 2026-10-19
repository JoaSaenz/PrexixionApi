package com.joa.prexixionapi.modules.vacacionProgramada.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.modules.personal.dto.PersonalDTO;
import com.joa.prexixionapi.modules.vacacionProgramada.dto.VacacionProgramadaDTO;
import com.joa.prexixionapi.modules.vacacionProgramada.repositories.VacacionProgramadaRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacacionProgramadaService {

    private final VacacionProgramadaRepository repository;

    public List<VacacionProgramadaDTO> list(Integer idArea, String dni, String fechaI, String fechaF) {
        return repository.list(idArea, dni, fechaI, fechaF);
    }

    public VacacionProgramadaDTO getOne(Integer id) {
        return repository.getOne(id);
    }

    public boolean exist(Integer id) {
        return repository.exist(id);
    }

    @Transactional
    public int insertUpdate(VacacionProgramadaDTO dto) {
        boolean exists = dto.getId() != null && repository.exist(dto.getId());

        // Si no se especificó estado, por defecto 1 (PROGRAMADA)
        if (dto.getIdEstado() == null || dto.getIdEstado() <= 0) {
            dto.setIdEstado(1);
        }

        if (exists) {
            // Edición: se respeta el estado elegido por el usuario (PROGRAMADA o
            // REPROGRAMADA)
            repository.update(dto);
            log.info("Vacación ID {} actualizada con idEstado={}", dto.getId(), dto.getIdEstado());
            return 2; // Actualizado
        } else {
            // Nuevo registro
            int newId = repository.insert(dto);
            dto.setId(newId);
            log.info("Nueva vacación registrada con ID {} para DNI {} con idEstado={}", newId, dto.getDni(),
                    dto.getIdEstado());
            return 1; // Registrado
        }
    }

    @Transactional
    public int delete(Integer id) {
        log.info("Eliminando vacación ID {}", id);
        return repository.delete(id);
    }

    public List<Map<String, Object>> getEstados() {
        return repository.getEstados();
    }

    public List<Map<String, Object>> getAreas() {
        return repository.getAreas();
    }

    public List<PersonalDTO> getPersonal(Integer idArea) {
        return repository.getPersonal(idArea);
    }
}
