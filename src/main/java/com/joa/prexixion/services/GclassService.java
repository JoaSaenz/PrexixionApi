package com.joa.prexixion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.entities.Gclass;
import com.joa.prexixion.repositories.GclassRepository;

@Service
public class GclassService {
    @Autowired
    private GclassRepository gclassRepository;

    public List<Gclass> list(String tableName) {
        return gclassRepository.list(tableName);
    }

    public List<Gclass> listOrderById(String tableName) {
        return gclassRepository.listOrderById(tableName);
    }

    public List<Gclass> listAbbrPlusEmptyOrderById(String tableName) {
        return gclassRepository.listAbbrPlusEmptyOrderById(tableName);
    }

    public List<Gclass> listPlusEmpty(String tableName) {
        return gclassRepository.listPlusEmpty(tableName);
    }

    public List<Gclass> listLoginOptions(String tableName) {
        return gclassRepository.listLoginOptions(tableName);
    }

    public List<Gclass> listLoginOptionsSpecials(String tableName) {
        return gclassRepository.listLoginOptionsSpecials(tableName);
    }

}
