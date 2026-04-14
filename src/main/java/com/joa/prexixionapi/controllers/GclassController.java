package com.joa.prexixionapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.services.GclassService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gclass")
@Slf4j
public class GclassController {

    @Autowired
    private GclassService gclassService;

    @GetMapping
    public List<Gclass> list(@RequestParam String tableName) {
        try {
            return gclassService.list(tableName);
        } catch (Exception e) {
            log.error("Error en list Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listString")
    public List<Gclass> listString(@RequestParam String tableName) {
        try {
            return gclassService.listString(tableName);
        } catch (Exception e) {
            log.error("Error en listString Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listOrderById")
    public List<Gclass> listOrderById(@RequestParam String tableName) {
        try {
            return gclassService.listOrderById(tableName);
        } catch (Exception e) {
            log.error("Error en listOrderById Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/abbrPlusEmptyOrderById")
    public List<Gclass> listAbbrPlusEmptyOrderById(@RequestParam String tableName) {
        try {
            return gclassService.listAbbrPlusEmptyOrderById(tableName);
        } catch (Exception e) {
            log.error("Error en listAbbrPlusEmptyOrderById Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/plusEmpty")
    public List<Gclass> listPlusEmpty(@RequestParam String tableName) {
        try {
            return gclassService.listPlusEmpty(tableName);
        } catch (Exception e) {
            log.error("Error en listPlusEmpty Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/plusEmptyOrderById")
    public List<Gclass> listPlusEmptyOrderById(@RequestParam String tableName) {
        try {
            return gclassService.listPlusEmptyOrderById(tableName);
        } catch (Exception e) {
            log.error("Error en listPlusEmptyOrderById Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listLoginOptions")
    public List<Gclass> listLoginOptions(@RequestParam String tableName) {
        try {
            return gclassService.listLoginOptions(tableName);
        } catch (Exception e) {
            log.error("Error en listLoginOptions Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/listLoginOptionsSpecials")
    public List<Gclass> listLoginOptionsSpecials(@RequestParam String tableName) {
        try {
            return gclassService.listLoginOptionsSpecials(tableName);
        } catch (Exception e) {
            log.error("Error en listLoginOptionsSpecials Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/abbr")
    public List<Gclass> listAbbr(@RequestParam String tableName) {
        try {
            return gclassService.listAbbr(tableName);
        } catch (Exception e) {
            log.error("Error en listAbbr Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @GetMapping("/codigoDescripcion")
    public List<Gclass> listCodigoDescripcion(@RequestParam String tableName) {
        try {
            return gclassService.listCodigoDescripcion(tableName);
        } catch (Exception e) {
            log.error("Error en listCodigoDescripcion Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @PostMapping("/insertUpdate")
    public int insertUpdate(@RequestParam String tableName, @RequestBody Gclass obj) {
        try {
            return gclassService.insertUpdate(tableName, obj);
        } catch (Exception e) {
            log.error("Error en insertUpdate Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @PostMapping("/insertUpdate2")
    public int insertUpdate2(@RequestParam String tableName, @RequestBody Gclass obj) {
        try {
            return gclassService.insertUpdate2(tableName, obj);
        } catch (Exception e) {
            log.error("Error en insertUpdate2 Gclass para tabla: {}", tableName, e);
            throw e;
        }
    }

    @DeleteMapping("/delete")
    public int delete(@RequestParam String tableName, @RequestParam int id) {
        try {
            return gclassService.delete(tableName, id);
        } catch (Exception e) {
            log.error("Error en delete Gclass para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/existe")
    public boolean existe(@RequestParam String tableName, @RequestParam int id) {
        try {
            return gclassService.existe(tableName, id);
        } catch (Exception e) {
            log.error("Error en existe Gclass para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/getOne")
    public Gclass getOne(@RequestParam String tableName, @RequestParam int id) {
        try {
            return gclassService.getOne(tableName, id);
        } catch (Exception e) {
            log.error("Error en getOne Gclass para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/getOne2")
    public Gclass getOne2(@RequestParam String tableName, @RequestParam int id) {
        try {
            return gclassService.getOne2(tableName, id);
        } catch (Exception e) {
            log.error("Error en getOne2 Gclass para tabla: {} y id: {}", tableName, id, e);
            throw e;
        }
    }

    @GetMapping("/credenciales")
    public List<Gclass> listCredenciales(@RequestParam int id) {
        try {
            return gclassService.listCredenciales(id);
        } catch (Exception e) {
            log.error("Error en listCredenciales Gclass para id: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/vlanIp")
    public Gclass vlanIp(@RequestParam int ip) {
        try {
            return gclassService.vlanIp(ip);
        } catch (Exception e) {
            log.error("Error en vlanIp Gclass para ip: {}", ip, e);
            throw e;
        }
    }

    @GetMapping("/fromTwoTables")
    public List<Gclass> listFromTwoTables(@RequestParam String tabla1, @RequestParam String tabla2,
            @RequestParam int id) {
        try {
            return gclassService.listFromTwoTables(tabla1, tabla2, id);
        } catch (Exception e) {
            log.error("Error en listFromTwoTables Gclass - Tablas: {}/{} e id: {}", tabla1, tabla2, id, e);
            throw e;
        }
    }

    @GetMapping("/fromThreeTables")
    public List<Gclass> listFromThreeTables(@RequestParam String tabla1, @RequestParam String tabla2,
            @RequestParam int id1, @RequestParam int id2) {
        try {
            return gclassService.listFromThreeTables(tabla1, tabla2, id1, id2);
        } catch (Exception e) {
            log.error("Error en listFromThreeTables Gclass - Tablas: {}/{} e IDs: {}/{}", tabla1, tabla2, id1, id2, e);
            throw e;
        }
    }

}
