package com.joa.prexixionapi.modules.jobSunat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixionapi.modules.jobSunat.dto.SunatBuzonDataTablesRequest;
import com.joa.prexixionapi.modules.jobSunat.dto.SunatBuzonDataTablesResponse;
import com.joa.prexixionapi.modules.jobSunat.repositories.SunatBuzonRepository;

@Service
public class SunatBuzonService {

    @Autowired
    private SunatBuzonRepository sunatBuzonRepository;

    public SunatBuzonDataTablesResponse list(SunatBuzonDataTablesRequest req) {
        SunatBuzonDataTablesResponse response = new SunatBuzonDataTablesResponse();
        
        response.setData(sunatBuzonRepository.list(req));
        
        return response;
    }
}
