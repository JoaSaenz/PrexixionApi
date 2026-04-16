package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.CronogramaPDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CronogramaPDTRepository extends JpaRepository<CronogramaPDT, Object> {
    Optional<CronogramaPDT> findByAnioAndMes(String anio, String mes);
}
