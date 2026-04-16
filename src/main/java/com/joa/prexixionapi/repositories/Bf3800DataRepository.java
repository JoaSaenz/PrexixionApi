package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.Bf3800Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Bf3800DataRepository extends JpaRepository<Bf3800Data, Object> {
    Optional<Bf3800Data> findByIdClienteAndAnioAndMes(String idCliente, String anio, String mes);
}
