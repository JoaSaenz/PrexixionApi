package com.joa.prexixionapi.modules.personal.controllers;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.modules.personal.dto.PersonalDTO;
import com.joa.prexixionapi.modules.personal.services.PersonalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
@Slf4j
public class PersonalController {

    private final PersonalService personalService;

    @GetMapping("/statuses")
    public ResponseEntity<List<GclassDTO>> getStatuses() {
        return ResponseEntity.ok(personalService.getPersonalStatuses());
    }

    @GetMapping("/types")
    public ResponseEntity<List<GclassDTO>> getTypes() {
        return ResponseEntity.ok(personalService.getPersonalTypes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonalDTO>> searchPersonal(@RequestParam String query) {
        return ResponseEntity.ok(personalService.searchPersonal(query));
    }

    @GetMapping("/responsables")
    public ResponseEntity<List<PersonalDTO>> getResponsablesPeriodo(
            @RequestParam String tabla,
            @RequestParam String anio,
            @RequestParam String mes) {
        return ResponseEntity.ok(personalService.getResponsablesPeriodo(tabla, anio, mes));
    }
}
