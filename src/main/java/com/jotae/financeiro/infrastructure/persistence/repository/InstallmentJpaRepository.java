package com.jotae.financeiro.infrastructure.persistence.repository;

import com.jotae.financeiro.infrastructure.persistence.entity.InstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentJpaRepository extends JpaRepository<InstallmentEntity, UUID> {
    List<InstallmentEntity> findByBill_User_IdAndDueDateBetween(UUID userId, LocalDate start, LocalDate end);
}
