package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.Bf3800Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Bf3800RegistroRepository extends JpaRepository<Bf3800Registro, Integer> {
    List<Bf3800Registro> findByIdClienteAndAnioAndMesOrderByNroRegistroAsc(String idCliente, String anio, String mes);
}
