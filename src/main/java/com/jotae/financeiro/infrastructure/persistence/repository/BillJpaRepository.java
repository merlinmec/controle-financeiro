package com.jotae.financeiro.infrastructure.persistence.repository;

import com.jotae.financeiro.infrastructure.persistence.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillJpaRepository extends JpaRepository<BillEntity, UUID> {
    List<BillEntity> findByUser_Id(UUID userId);
}
