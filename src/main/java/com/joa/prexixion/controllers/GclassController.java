package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.services.GclassService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gclass")
public class GclassController {

    @Autowired
    private GclassService gclassService;

    @GetMapping
    public List<Gclass> list(@RequestParam String tableName) {
        return gclassService.list(tableName);
    }

    @GetMapping("/listOrderById")
    public List<Gclass> listOrderById(@RequestParam String tableName) {
        return gclassService.listOrderById(tableName);
    }

    @GetMapping("/abbrPlusEmptyOrderById")
    public List<Gclass> listAbbrPlusEmptyOrderById(@RequestParam String tableName) {
        return gclassService.listAbbrPlusEmptyOrderById(tableName);
    }

    @GetMapping("/plusEmpty")
    public List<Gclass> listPlusEmpty(@RequestParam String tableName) {
        return gclassService.listPlusEmpty(tableName);
    }

    @GetMapping("/plusEmptyOrderById")
    public List<Gclass> listPlusEmptyOrderById(@RequestParam String tableName) {
        return gclassService.listPlusEmptyOrderById(tableName);
    }

    @GetMapping("/listLoginOptions")
    public List<Gclass> listLoginOptions(@RequestParam String tableName) {
        return gclassService.listLoginOptions(tableName);
    }

    @GetMapping("/listLoginOptionsSpecials")
    public List<Gclass> listLoginOptionsSpecials(@RequestParam String tableName) {
        return gclassService.listLoginOptionsSpecials(tableName);
    }

    @GetMapping("/abbr")
    public List<Gclass> listAbbr(@RequestParam String tableName) {
        return gclassService.listAbbr(tableName);
    }

    @GetMapping("/codigoDescripcion")
    public List<Gclass> listCodigoDescripcion(@RequestParam String tableName) {
        return gclassService.listCodigoDescripcion(tableName);
    }

    @PostMapping("/insertUpdate")
    public int insertUpdate(@RequestParam String tableName, @RequestBody Gclass obj) {
        return gclassService.insertUpdate(tableName, obj);
    }

    @PostMapping("/insertUpdate2")
    public int insertUpdate2(@RequestParam String tableName, @RequestBody Gclass obj) {
        return gclassService.insertUpdate2(tableName, obj);
    }

    @DeleteMapping("/delete")
    public int delete(@RequestParam String tableName, @RequestParam int id) {
        return gclassService.delete(tableName, id);
    }

    @GetMapping("/existe")
    public boolean existe(@RequestParam String tableName, @RequestParam int id) {
        return gclassService.existe(tableName, id);
    }

    @GetMapping("/getOne")
    public Gclass getOne(@RequestParam String tableName, @RequestParam int id) {
        return gclassService.getOne(tableName, id);
    }

    @GetMapping("/getOne2")
    public Gclass getOne2(@RequestParam String tableName, @RequestParam int id) {
        return gclassService.getOne2(tableName, id);
    }

    @GetMapping("/credenciales")
    public List<Gclass> listCredenciales(@RequestParam int id) {
        return gclassService.listCredenciales(id);
    }

    @GetMapping("/vlanIp")
    public Gclass vlanIp(@RequestParam int ip) {
        return gclassService.vlanIp(ip);
    }

    @GetMapping("/fromTwoTables")
    public List<Gclass> listFromTwoTables(@RequestParam String tabla1, @RequestParam String tabla2, @RequestParam int id) {
        return gclassService.listFromTwoTables(tabla1, tabla2, id);
    }

    @GetMapping("/fromThreeTables")
    public List<Gclass> listFromThreeTables(@RequestParam String tabla1, @RequestParam String tabla2, @RequestParam int id1, @RequestParam int id2) {
        return gclassService.listFromThreeTables(tabla1, tabla2, id1, id2);
    }

}
