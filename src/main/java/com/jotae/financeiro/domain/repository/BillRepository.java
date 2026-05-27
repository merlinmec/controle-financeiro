package com.jotae.financeiro.domain.repository;

import com.jotae.financeiro.domain.model.Bill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository {
    Bill save(Bill bill);
    Optional<Bill> findById(UUID id);
    List<Bill> findByUserId(UUID userId);
}
