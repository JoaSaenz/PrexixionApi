package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.entities.XentraReporte;
import com.joa.prexixion.services.XentraReporteService;

@RestController
@RequestMapping("/api/xentraReporte")
public class XentraReporteController {

    @Autowired
    XentraReporteService xentraReporteService;

    @GetMapping("/{idPuesto}/{idArea}/{dni}/{fechaInicial}/{fechaFinal}")
    public List<XentraReporte> list(
            @PathVariable int idPuesto,
            @PathVariable int idArea,
            @PathVariable String dni,
            @PathVariable String fechaInicial,
            @PathVariable String fechaFinal) {
        return xentraReporteService.list(idPuesto, idArea, dni, fechaInicial, fechaFinal);
    }
}
