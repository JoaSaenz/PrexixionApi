package com.joa.prexixionapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.joa.prexixionapi.entities.XentraData;

@Repository
public interface XentraDataRepository extends JpaRepository<XentraData, Integer> {

}
