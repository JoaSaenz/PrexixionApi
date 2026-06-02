package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.LoginVenta;
import com.joa.prexixionapi.entities.LoginVentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginVentaJpaRepository extends JpaRepository<LoginVenta, LoginVentaId> {
}
