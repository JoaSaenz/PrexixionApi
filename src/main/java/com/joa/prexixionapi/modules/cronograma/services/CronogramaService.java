package com.joa.prexixionapi.modules.cronograma.services;

import com.joa.prexixionapi.modules.cronograma.dto.CronogramaPDTDTO;
import com.joa.prexixionapi.modules.cronograma.repositories.CronogramaPDTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CronogramaService {

    private final CronogramaPDTRepository cronogramaRepository;

    public String getVencimiento(String anio, String mes, String y) {
        Optional<CronogramaPDTDTO> cronogramaOpt = cronogramaRepository.findByAnioAndMes(anio, mes);
        if (cronogramaOpt.isEmpty()) {
            return null;
        }

        CronogramaPDTDTO cronograma = cronogramaOpt.get();
        return switch (y.toLowerCase()) {
            case "0" -> cronograma.getFecha0();
            case "1" -> cronograma.getFecha1();
            case "2" -> cronograma.getFecha2();
            case "3" -> cronograma.getFecha3();
            case "4" -> cronograma.getFecha4();
            case "5" -> cronograma.getFecha5();
            case "6" -> cronograma.getFecha6();
            case "7" -> cronograma.getFecha7();
            case "8" -> cronograma.getFecha8();
            case "9" -> cronograma.getFecha9();
            case "b" -> cronograma.getFechab();
            default -> null;
        };
    }
}
