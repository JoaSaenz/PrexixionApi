package com.joa.prexixionapi.modules.personal.services;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.dto.PersonalDTO;
import com.joa.prexixionapi.modules.personal.repositories.PersonalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalService {

    private final PersonalRepository personalRepository;

    public List<GclassDTO> getPersonalStatuses() {
        return personalRepository.getPersonalStatuses();
    }

    public List<GclassDTO> getPersonalTypes() {
        return personalRepository.getPersonalTypes();
    }

    public List<PersonalDTO> searchPersonal(String query) {
        log.info("Searching personal with query: {}", query);
        return personalRepository.searchPersonal(query);
    }

    public List<PersonalDTO> getResponsablesPeriodo(String tabla, String anio, String mes) {
        return personalRepository.getResponsablesPeriodo(tabla, anio, mes);
    }
}
