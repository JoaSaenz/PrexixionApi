package com.joa.prexixionapi.repositories;

import com.joa.prexixionapi.entities.LoginCompra;
import com.joa.prexixionapi.entities.LoginCompraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginCompraJpaRepository extends JpaRepository<LoginCompra, LoginCompraId> {
}
