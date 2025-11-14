package com.joa.prexixion.controllers;

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

import com.joa.prexixion.dto.XentraDataDTO;
import com.joa.prexixion.services.XentraService;

@RestController
@RequestMapping("/api/xentra")
public class XentraController {

    @Autowired
    XentraService xentraService;

    @GetMapping("/{idPuesto}/{idArea}")
    public List<XentraDataDTO> list(
            @PathVariable int idPuesto,
            @PathVariable int idArea) {
        return xentraService.list(idPuesto, idArea);
    }

    @PostMapping
    public int insert(@RequestBody XentraDataDTO xentraDTO) {
        int rpta = 0;
        rpta = xentraService.insertarXentra(xentraDTO);
        return rpta;
    }

    @PutMapping("/{id}")
    public int edit(@PathVariable int id, @RequestParam String fechaFinFront) {
        int rpta = 0;
        System.out.println(id);
        System.out.println(fechaFinFront);
        rpta = xentraService.editarXentra(id, fechaFinFront);
        return rpta;
    }

    @GetMapping("/{id}")
    public XentraDataDTO getOne(@PathVariable int id) {
        return xentraService.getOne(id);
    }

    @GetMapping("/getListXentraFechas/{idPuesto}/{idArea}/{idSubArea}/{dni}")
    public List<XentraDataDTO> getListXentraFechas(
            @PathVariable int idPuesto,
            @PathVariable int idArea,
            @PathVariable int idSubArea,
            @PathVariable String dni) {
        return xentraService.getListXentraFechas(idPuesto, idArea, idSubArea, dni);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable int id) {
        System.out.println("id: " + id);

        return xentraService.delete(id);
    }

}
