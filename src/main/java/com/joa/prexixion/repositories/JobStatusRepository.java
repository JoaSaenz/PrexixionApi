package com.joa.prexixion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joa.prexixion.entities.JobStatus;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, Long> {
    Optional<JobStatus> findTopByNombreJobOrderByHoraInicioDesc(String nombreJob);

    List<JobStatus> findTop7ByNombreJobOrderByHoraInicioDesc(String nombreJob);
}
