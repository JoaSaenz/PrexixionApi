package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.LoginProcesos;
import com.joa.prexixionapi.entities.LoginProcesosId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginProcesosJpaRepository extends JpaRepository<LoginProcesos, LoginProcesosId> {
}
