package com.joa.prexixion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.joa.prexixion.entities.XentraFecha;

import java.util.List;

@Repository
public interface XentraFechaRepository extends JpaRepository<XentraFecha, Integer> {

    List<XentraFecha> findByXentra_Id(Integer idXentra); // buscar todas las fechas de un xentra

    void deleteByXentra_Id(Integer idXentra); // eliminar todas las fechas de un xentra
}
