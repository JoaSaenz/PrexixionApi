package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Integer> {
    List<Beneficiario> findByIdClienteAndAnioAndMes(String idCliente, String anio, String mes);
}
