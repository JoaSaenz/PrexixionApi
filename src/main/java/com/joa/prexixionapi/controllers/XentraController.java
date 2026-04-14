package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.dto.XentraDataDTO;
import com.joa.prexixionapi.services.XentraService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/xentra")
@Slf4j
public class XentraController {

    @Autowired
    XentraService xentraService;

    @GetMapping("/{idPuesto}/{idArea}")
    public List<XentraDataDTO> list(
            @PathVariable int idPuesto,
            @PathVariable int idArea) {
        try {
            return xentraService.list(idPuesto, idArea);
        } catch (Exception e) {
            log.error("Error al listar datos Xentra - Puesto: {}, Area: {}", idPuesto, idArea, e);
            throw e;
        }
    }

    @PostMapping
    public int insert(@RequestBody XentraDataDTO xentraDTO) {
        try {
            return xentraService.insertarXentra(xentraDTO);
        } catch (Exception e) {
            log.error("Error al insertar dato Xentra: {}", xentraDTO.getNombre(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public int edit(@PathVariable int id, @RequestParam String fechaFinFront) {
        try {
            return xentraService.editarXentra(id, fechaFinFront);
        } catch (Exception e) {
            log.error("Error al editar Xentra id: {}, fechaFinFront: {}", id, fechaFinFront, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public XentraDataDTO getOne(@PathVariable int id) {
        try {
            return xentraService.getOne(id);
        } catch (Exception e) {
            log.error("Error al obtener dato Xentra id: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/getListXentraFechas/{idPuesto}/{idArea}/{idSubArea}/{dni}")
    public List<XentraDataDTO> getListXentraFechas(
            @PathVariable int idPuesto,
            @PathVariable int idArea,
            @PathVariable int idSubArea,
            @PathVariable String dni) {
        try {
            return xentraService.getListXentraFechas(idPuesto, idArea, idSubArea, dni);
        } catch (Exception e) {
            log.error("Error al obtener lista Xentra fechas - Puesto: {}, Area: {}, SubArea: {}, DNI: {}",
                    idPuesto, idArea, idSubArea, dni, e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable int id) {
        try {
            return xentraService.delete(id);
        } catch (Exception e) {
            log.error("Error al eliminar Xentra id: {}", id, e);
            throw e;
        }
    }

}
