package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.entities.Gclass;
import com.joa.prexixionapi.repositories.GclassRepository;

@Service
public class GclassService {
    @Autowired
    private GclassRepository gclassRepository;

    public List<Gclass> list(String tableName) {
        return gclassRepository.list(tableName);
    }

    public List<Gclass> listString(String tableName) {
        return gclassRepository.listString(tableName);
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

    public List<Gclass> listPlusEmptyOrderById(String tableName) {
        return gclassRepository.listPlusEmptyOrderById(tableName);
    }

    public List<Gclass> listLoginOptions(String tableName) {
        return gclassRepository.listLoginOptions(tableName);
    }

    public List<Gclass> listLoginOptionsSpecials(String tableName) {
        return gclassRepository.listLoginOptionsSpecials(tableName);
    }

    public List<Gclass> listAbbr(String tableName) {
        return gclassRepository.listAbbr(tableName);
    }

    public List<Gclass> listCodigoDescripcion(String tableName) {
        return gclassRepository.listCodigoDescripcion(tableName);
    }

    public int insertUpdate(String tableName, Gclass obj) {
        return gclassRepository.insertUpdate(tableName, obj);
    }

    public int insertUpdate2(String tableName, Gclass obj) {
        return gclassRepository.insertUpdate2(tableName, obj);
    }

    public int delete(String tableName, int id) {
        return gclassRepository.delete(tableName, id);
    }

    public boolean existe(String tableName, int id) {
        return gclassRepository.existe(tableName, id);
    }

    public Gclass getOne(String tableName, int id) {
        return gclassRepository.getOne(tableName, id);
    }

    public Gclass getOne2(String tableName, int id) {
        return gclassRepository.getOne2(tableName, id);
    }

    public List<Gclass> listCredenciales(int id) {
        return gclassRepository.listCredenciales(id);
    }

    public Gclass vlanIp(int ip) {
        return gclassRepository.vlanIp(ip);
    }

    public List<Gclass> listFromTwoTables(String tabla1, String tabla2, int id) {
        return gclassRepository.listFromTwoTables(tabla1, tabla2, id);
    }

    public List<Gclass> listFromThreeTables(String tabla1, String tabla2, int id1, int id2) {
        return gclassRepository.listFromThreeTables(tabla1, tabla2, id1, id2);
    }

}
