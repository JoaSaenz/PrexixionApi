package com.joa.prexixionapi.services;

import com.joa.prexixionapi.dto.SignerOsceDTO;
import com.joa.prexixionapi.dto.SignerOsceListDTO;
import com.joa.prexixionapi.dto.SignerOsceRequest;
import com.joa.prexixionapi.dto.SignerOsceTipoDTO;
import com.joa.prexixionapi.repositories.SignerOsceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignerOsceService {

    private final SignerOsceRepository repository;

    public List<SignerOsceListDTO> list(SignerOsceRequest request) {
        return repository.listForDataTable(request);
    }
    
    public List<SignerOsceListDTO> listExcel(SignerOsceRequest request) {
        return repository.listForExcel(request);
    }

    public SignerOsceDTO getOne(String idCliente) {
        return repository.getOne(idCliente);
    }
    
    public boolean exist(String idCliente) {
        return repository.exist(idCliente);
    }

    @Transactional
    public int insertUpdate(SignerOsceDTO dto) {
        boolean exists = repository.exist(dto.getIdCliente());
        int rpta;

        if (exists) {
            repository.update(dto);
            
            if (dto.getListSignerOsceTipo() != null && !dto.getListSignerOsceTipo().isEmpty()) {
                List<Integer> idsToKeep = dto.getListSignerOsceTipo().stream()
                        .map(SignerOsceTipoDTO::getId)
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toList());
                        
                repository.deleteTiposExcept(dto.getIdCliente(), idsToKeep);
                
                for (SignerOsceTipoDTO tipo : dto.getListSignerOsceTipo()) {
                    if (tipo.getId() != null && tipo.getId() > 0) {
                        repository.updateTipo(tipo);
                    } else {
                        tipo.setIdCliente(dto.getIdCliente());
                        repository.insertTipo(tipo);
                    }
                }
            } else {
                repository.deleteTipos(dto.getIdCliente());
            }
            rpta = 2; // Updated
        } else {
            repository.insert(dto);
            
            if (dto.getListSignerOsceTipo() != null) {
                for (SignerOsceTipoDTO tipo : dto.getListSignerOsceTipo()) {
                    tipo.setIdCliente(dto.getIdCliente());
                    repository.insertTipo(tipo);
                }
            }
            rpta = 1; // Inserted
        }
        
        return rpta;
    }

    @Transactional
    public int delete(String idCliente) {
        try {
            repository.deleteTipos(idCliente);
            repository.delete(idCliente);
            return 3;
        } catch (Exception e) {
            log.error("Error deleting SignerOsce: " + idCliente, e);
            return 0;
        }
    }
}
