package com.joa.prexixion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.joa.prexixion.entities.XentraData;

@Repository
public interface XentraDataRepository extends JpaRepository<XentraData, Integer> {

}
