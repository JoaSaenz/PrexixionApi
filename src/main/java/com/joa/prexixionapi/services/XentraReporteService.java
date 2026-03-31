package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.entities.XentraReporte;
import com.joa.prexixionapi.repositories.XentraReporteRepository;

@Service
public class XentraReporteService {
    
    @Autowired
    XentraReporteRepository xentraReporteRepository;

    public List<XentraReporte> list(int idPuesto, int idArea, String dni, String fechaInicial, String fechaFinal) {
        return xentraReporteRepository.list(idPuesto, idArea, dni, fechaInicial, fechaFinal);
    }
}
