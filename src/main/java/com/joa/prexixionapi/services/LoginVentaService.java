package com.joa.prexixionapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joa.prexixionapi.entities.Cliente;
import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.entities.LoginVentaId;
import com.joa.prexixionapi.repositories.LoginVentaJpaRepository;
import com.joa.prexixionapi.repositories.LoginVentaRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginVentaService {

    @Autowired
    private LoginVentaRepository loginVentaRepository;

    @Autowired
    private LoginVentaJpaRepository loginVentaJpaRepository;

    public List<Cliente> list(String anio, String mes, String estados, String grupos) {
        return loginVentaRepository.list(anio, mes, estados, grupos);
    }

    public Cliente getOne(String ruc, String anio, String mes) {
        Cliente clie = loginVentaRepository.getOne(ruc, anio, mes);
        
        if (clie == null) {
            clie = new Cliente();
            clie.setRuc(ruc);
        }

        if (clie.getLoginVenta() == null) {
            LoginVenta emptyLv = new LoginVenta();
            emptyLv.setVersion(0);
            clie.setLoginVenta(emptyLv);
        } else if (clie.getLoginVenta().getVersion() == 0) {
            clie.getLoginVenta().setVersion(0);
        }
        
        return clie;
    }

    @Transactional
    public int insertUpdate(Cliente clie) {
        try {
            LoginVenta lvInput = clie.getLoginVenta();
            LoginVentaId id = new LoginVentaId(clie.getRuc(), lvInput.getAnio(), lvInput.getMes());
            lvInput.setId(id);

            // Legacy returns 1 for insert, 2 for update, 10 for optimistic lock fail
            boolean exists = loginVentaJpaRepository.existsById(id);

            if (exists) {
                // JPA will handle the versioning update
                loginVentaJpaRepository.save(lvInput);
                return 2; // update
            } else {
                // Force version to 1 for new records as legacy did
                lvInput.setVersion(1);
                loginVentaJpaRepository.save(lvInput);
                return 1; // insert
            }
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock exception on LoginVenta: {}", clie.getRuc());
            return 10;
        } catch (Exception e) {
            log.error("Error inserting/updating LoginVenta", e);
            throw e;
        }
    }
}
