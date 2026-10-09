package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.PersonDTO;
import com.joa.prexixionapi.dto.VacacionProgramadaDTO;
import com.joa.prexixionapi.repositories.VacacionProgramadaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (exists) {
            // Edición: pasa al estado 2: REPROGRAMADA
            dto.setIdEstado(2);
            repository.update(dto);
            log.info("Vacación ID {} actualizada a estado REPROGRAMADA (idEstado=2)", dto.getId());
            return 2; // Actualizado
        } else {
            // Nuevo registro: nace como 1: PROGRAMADA
            if (dto.getIdEstado() == null || dto.getIdEstado() <= 0) {
                dto.setIdEstado(1);
            }
            int newId = repository.insert(dto);
            dto.setId(newId);
            log.info("Nueva vacación registrada con ID {} para DNI {} (idEstado=1)", newId, dto.getDni());
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

    public List<PersonDTO> getPersonal(Integer idArea) {
        return repository.getPersonal(idArea);
    }
}
