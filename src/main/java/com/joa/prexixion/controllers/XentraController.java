package com.joa.prexixion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    //@GetMapping
    //public List<Gclass> list() {
    //    return gclassService.list(tableName);
    //}

    @PostMapping
    public int insert(@RequestBody XentraRequest request){
        int rpta = 0;
        rpta = xentraService.guardarFechas(request);
        return rpta;
    }

    

}
