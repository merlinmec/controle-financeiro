package com.jotae.financeiro.domain.repository;

import com.jotae.financeiro.domain.model.Installment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstallmentRepository {
    Installment save(Installment installment);
    List<Installment> saveAll(List<Installment> installments);
    Optional<Installment> findById(UUID id);
    
    List<Installment> findByBillUserIdAndDueDateBetween(UUID userId, LocalDate start, LocalDate end);
}
