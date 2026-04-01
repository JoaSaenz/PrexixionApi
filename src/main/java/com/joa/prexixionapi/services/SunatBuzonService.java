package com.joa.prexixionapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.dto.SunatBuzonDataTablesRequest;
import com.joa.prexixionapi.dto.SunatBuzonDataTablesResponse;
import com.joa.prexixionapi.repositories.SunatBuzonRepository;

@Service
public class SunatBuzonService {

    @Autowired
    private SunatBuzonRepository sunatBuzonRepository;

    public SunatBuzonDataTablesResponse listServerSide(SunatBuzonDataTablesRequest req) {
        SunatBuzonDataTablesResponse response = new SunatBuzonDataTablesResponse();
        response.setDraw(req.getDraw());
        
        int total = sunatBuzonRepository.countServerSide(req);
        response.setRecordsTotal(total);
        response.setRecordsFiltered(total);
        
        response.setData(sunatBuzonRepository.listServerSide(req));
        
        return response;
    }
}
