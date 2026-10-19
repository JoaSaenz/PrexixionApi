package com.joa.prexixionapi.common.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.services.GclassService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixionapi.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/gclass")
@Slf4j
public class GclassController {

    @Autowired
    private GclassService gclassService;

    @GetMapping
    public ResponseEntity<List<GclassDTO>> list(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.list(tableName));
        } catch (Exception e) {
            log.error("Error en list GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listString")
    public ResponseEntity<List<GclassDTO>> listString(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listString(tableName));
        } catch (Exception e) {
            log.error("Error en listString GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listOrderById")
    public ResponseEntity<List<GclassDTO>> listOrderById(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listOrderById(tableName));
        } catch (Exception e) {
            log.error("Error en listOrderById GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/abbrPlusEmptyOrderById")
    public ResponseEntity<List<GclassDTO>> listAbbrPlusEmptyOrderById(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listAbbrPlusEmptyOrderById(tableName));
        } catch (Exception e) {
            log.error("Error en listAbbrPlusEmptyOrderById GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/plusEmpty")
    public ResponseEntity<List<GclassDTO>> listPlusEmpty(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listPlusEmpty(tableName));
        } catch (Exception e) {
            log.error("Error en listPlusEmpty GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/plusEmptyOrderById")
    public ResponseEntity<List<GclassDTO>> listPlusEmptyOrderById(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listPlusEmptyOrderById(tableName));
        } catch (Exception e) {
            log.error("Error en listPlusEmptyOrderById GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listLoginOptions")
    public ResponseEntity<List<GclassDTO>> listLoginOptions(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listLoginOptions(tableName));
        } catch (Exception e) {
            log.error("Error en listLoginOptions GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listLoginOptionsSpecials")
    public ResponseEntity<List<GclassDTO>> listLoginOptionsSpecials(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listLoginOptionsSpecials(tableName));
        } catch (Exception e) {
            log.error("Error en listLoginOptionsSpecials GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/abbr")
    public ResponseEntity<List<GclassDTO>> listAbbr(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listAbbr(tableName));
        } catch (Exception e) {
            log.error("Error en listAbbr GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/codigoDescripcion")
    public ResponseEntity<List<GclassDTO>> listCodigoDescripcion(@RequestParam String tableName) {
        try {
            return ResponseEntity.ok(gclassService.listCodigoDescripcion(tableName));
        } catch (Exception e) {
            log.error("Error en listCodigoDescripcion GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @PostMapping("/insertUpdate")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate(@RequestParam String tableName, @RequestBody GclassDTO obj) {
        try {
            int result = gclassService.insertUpdate(tableName, obj);
            String message = result == 1 ? "Registro creado correctamente" : "Registro actualizado correctamente";
            return ResponseEntity.ok(new ApiResponse<>(true, message, result));
        } catch (Exception e) {
            log.error("Error en insertUpdate GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @PostMapping("/insertUpdate2")
    public ResponseEntity<ApiResponse<Integer>> insertUpdate2(@RequestParam String tableName, @RequestBody GclassDTO obj) {
        try {
            int result = gclassService.insertUpdate2(tableName, obj);
            String message = result == 1 ? "Registro creado correctamente" : "Registro actualizado correctamente";
            return ResponseEntity.ok(new ApiResponse<>(true, message, result));
        } catch (Exception e) {
            log.error("Error en insertUpdate2 GclassDTO para tabla: {}", tableName, e);
            throw e;
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam String tableName, @RequestParam int id) {
        try {
            int result = gclassService.delete(tableName, id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Registro eliminado correctamente", result));
        } catch (Exception e) {
            log.error("Error en delete GclassDTO para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existe(@RequestParam String tableName, @RequestParam int id) {
        try {
            return ResponseEntity.ok(gclassService.existe(tableName, id));
        } catch (Exception e) {
            log.error("Error en existe GclassDTO para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/getOne")
    public ResponseEntity<GclassDTO> getOne(@RequestParam String tableName, @RequestParam int id) {
        try {
            GclassDTO obj = gclassService.getOne(tableName, id);
            return obj != null ? ResponseEntity.ok(obj) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error en getOne GclassDTO para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/getOne2")
    public ResponseEntity<GclassDTO> getOne2(@RequestParam String tableName, @RequestParam int id) {
        try {
            GclassDTO obj = gclassService.getOne2(tableName, id);
            return obj != null ? ResponseEntity.ok(obj) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error en getOne2 GclassDTO para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/credenciales")
    public ResponseEntity<List<GclassDTO>> listCredenciales(@RequestParam int id) {
        try {
            return ResponseEntity.ok(gclassService.listCredenciales(id));
        } catch (Exception e) {
            log.error("Error en listCredenciales GclassDTO para id: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/vlanIp")
    public ResponseEntity<GclassDTO> vlanIp(@RequestParam int ip) {
        try {
            GclassDTO obj = gclassService.vlanIp(ip);
            return obj != null ? ResponseEntity.ok(obj) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error en vlanIp GclassDTO para ip: {}", ip, e);
            throw e;
        }
    }

    @GetMapping("/fromTwoTables")
    public ResponseEntity<List<GclassDTO>> listFromTwoTables(@RequestParam String tabla1, @RequestParam String tabla2,
            @RequestParam int id) {
        try {
            return ResponseEntity.ok(gclassService.listFromTwoTables(tabla1, tabla2, id));
        } catch (Exception e) {
            log.error("Error en listFromTwoTables GclassDTO - Tablas: {}/{} e id: {}", tabla1, tabla2, id, e);
            throw e;
        }
    }

    @GetMapping("/fromThreeTables")
    public ResponseEntity<List<GclassDTO>> listFromThreeTables(@RequestParam String tabla1, @RequestParam String tabla2,
            @RequestParam int id1, @RequestParam int id2) {
        try {
            return ResponseEntity.ok(gclassService.listFromThreeTables(tabla1, tabla2, id1, id2));
        } catch (Exception e) {
            log.error("Error en listFromThreeTables GclassDTO - Tablas: {}/{} e IDs: {}/{}", tabla1, tabla2, id1, id2, e);
            throw e;
        }
    }

}
