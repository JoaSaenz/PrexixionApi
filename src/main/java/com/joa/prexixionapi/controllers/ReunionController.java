package com.joa.prexixionapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.joa.prexixionapi.dto.ReunionDataTablesRequest;
import com.joa.prexixionapi.dto.ReunionDataTablesResponse;
import com.joa.prexixionapi.dto.ReunionDTO;
import com.joa.prexixionapi.services.ReunionService;

@RestController
@RequestMapping("/api/reuniones")
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    @GetMapping("/server-side")
    public ReunionDataTablesResponse listServerSide(ReunionDataTablesRequest req) {
        return reunionService.listServerSide(req);
    }

    @GetMapping("/{idReunion}")
    public ReunionDTO getReunion(@PathVariable int idReunion) {
        return reunionService.getReunion(idReunion);
    }

    @PostMapping
    public ReunionDTO insertUpdate(@RequestBody ReunionDTO reunion) {
        return reunionService.insertUpdate(reunion);
    }

    @DeleteMapping("/{idReunion}")
    public void deleteReunion(@PathVariable int idReunion) {
        reunionService.deleteReunion(idReunion);
    }
}
