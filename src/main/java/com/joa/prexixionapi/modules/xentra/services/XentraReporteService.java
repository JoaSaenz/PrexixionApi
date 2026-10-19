package com.joa.prexixionapi.modules.xentra.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.xentra.dto.XentraReporteDTO;
import com.joa.prexixionapi.modules.xentra.repositories.XentraReporteRepository;

@Service
public class XentraReporteService {
    
    @Autowired
    XentraReporteRepository xentraReporteRepository;

    public List<XentraReporteDTO> list(int idPuesto, int idArea, String dni, String fechaInicial, String fechaFinal) {
        return xentraReporteRepository.list(idPuesto, idArea, dni, fechaInicial, fechaFinal);
    }
}
