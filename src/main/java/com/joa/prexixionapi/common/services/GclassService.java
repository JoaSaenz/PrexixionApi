package com.joa.prexixionapi.common.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.common.dto.GclassDTO;
import com.joa.prexixionapi.common.repositories.GclassRepository;

@Service
public class GclassService {
    @Autowired
    private GclassRepository gclassRepository;

    public List<GclassDTO> list(String tableName) {
        return gclassRepository.list(tableName);
    }

    public List<GclassDTO> listString(String tableName) {
        return gclassRepository.listString(tableName);
    }

    public List<GclassDTO> listOrderById(String tableName) {
        return gclassRepository.listOrderById(tableName);
    }

    public List<GclassDTO> listAbbrPlusEmptyOrderById(String tableName) {
        return gclassRepository.listAbbrPlusEmptyOrderById(tableName);
    }

    public List<GclassDTO> listPlusEmpty(String tableName) {
        return gclassRepository.listPlusEmpty(tableName);
    }

    public List<GclassDTO> listPlusEmptyOrderById(String tableName) {
        return gclassRepository.listPlusEmptyOrderById(tableName);
    }

    public List<GclassDTO> listLoginOptions(String tableName) {
        return gclassRepository.listLoginOptions(tableName);
    }

    public List<GclassDTO> listLoginOptionsSpecials(String tableName) {
        return gclassRepository.listLoginOptionsSpecials(tableName);
    }

    public List<GclassDTO> listAbbr(String tableName) {
        return gclassRepository.listAbbr(tableName);
    }

    public List<GclassDTO> listCodigoDescripcion(String tableName) {
        return gclassRepository.listCodigoDescripcion(tableName);
    }

    public int insertUpdate(String tableName, GclassDTO obj) {
        return gclassRepository.insertUpdate(tableName, obj);
    }

    public int insertUpdate2(String tableName, GclassDTO obj) {
        return gclassRepository.insertUpdate2(tableName, obj);
    }

    public int delete(String tableName, int id) {
        return gclassRepository.delete(tableName, id);
    }

    public boolean existe(String tableName, int id) {
        return gclassRepository.existe(tableName, id);
    }

    public GclassDTO getOne(String tableName, int id) {
        return gclassRepository.getOne(tableName, id);
    }

    public GclassDTO getOne2(String tableName, int id) {
        return gclassRepository.getOne2(tableName, id);
    }

    public List<GclassDTO> listCredenciales(int id) {
        return gclassRepository.listCredenciales(id);
    }

    public GclassDTO vlanIp(int ip) {
        return gclassRepository.vlanIp(ip);
    }

    public List<GclassDTO> listFromTwoTables(String tabla1, String tabla2, int id) {
        return gclassRepository.listFromTwoTables(tabla1, tabla2, id);
    }

    public List<GclassDTO> listFromThreeTables(String tabla1, String tabla2, int id1, int id2) {
        return gclassRepository.listFromThreeTables(tabla1, tabla2, id1, id2);
    }

}
