package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.entities.XentraRequest;
import com.joa.prexixion.services.XentraService;

@RestController
@RequestMapping("/api/xentra")
public class XentraController {

    @Autowired
    XentraService xentraService;

    @GetMapping("/{idPuesto}/{idArea}")
    public List<XentraRequest> list(
        @PathVariable int idPuesto,
            @PathVariable int idArea) {
        return xentraService.list(idPuesto, idArea);
    }

    @PostMapping
    public int insert(@RequestBody XentraRequest request) {
        int rpta = 0;
        rpta = xentraService.guardarFechas(request);
        return rpta;
    }

    @GetMapping("/{id}")
    public XentraRequest getOne(@PathVariable int id) {
        return xentraService.getOne(id);
    }

    @GetMapping("/getListXentraFechas/{idPuesto}/{idArea}/{idSubArea}/{dni}")
    public List<XentraRequest> getListXentraFechas(
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
