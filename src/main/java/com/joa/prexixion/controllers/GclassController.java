package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.services.GclassService;

import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/listLoginOptions")
    public List<Gclass> listLoginOptions(@RequestParam String tableName) {
        return gclassService.listLoginOptions(tableName);
    }

    @GetMapping("/listLoginOptionsSpecials")
    public List<Gclass> listLoginOptionsSpecials(@RequestParam String tableName) {
        return gclassService.listLoginOptionsSpecials(tableName);
    }

}
