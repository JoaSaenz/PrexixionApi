package com.joa.prexixionapi.modules.depreciacion.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.joa.prexixionapi.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.joa.prexixionapi.modules.depreciacion.dto.ActivoTipoDTO;
import com.joa.prexixionapi.modules.depreciacion.controllers.ActivoTipoController;
import com.joa.prexixionapi.modules.depreciacion.services.ActivoTipoService;

@RestController
@RequestMapping("/api/activos/tipos")
@RequiredArgsConstructor
@Slf4j
public class ActivoTipoController {

    private final ActivoTipoService service;

    @GetMapping
    public ResponseEntity<List<ActivoTipoDTO>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivoTipoDTO> getById(@PathVariable String id) throws Exception  {
        ActivoTipoDTO dto = service.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> save(@RequestBody ActivoTipoDTO dto) {
        service.saveActivoTipo(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tipo de activo guardado correctamente", null));
    }
}
