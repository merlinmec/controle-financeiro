package com.jotae.financeiro.infrastructure.persistence.adapter;

import com.jotae.financeiro.domain.model.Installment;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
import com.jotae.financeiro.infrastructure.persistence.mapper.InstallmentMapper;
import com.jotae.financeiro.infrastructure.persistence.repository.InstallmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InstallmentRepositoryAdapter implements InstallmentRepository {

    private final InstallmentJpaRepository jpaRepository;

    @Override
    public Installment save(Installment installment) {
        var entity = InstallmentMapper.toEntity(installment);
        return InstallmentMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Installment> saveAll(List<Installment> installments) {
        var entities = installments.stream().map(InstallmentMapper::toEntity).collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream().map(InstallmentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Installment> findById(UUID id) {
        return jpaRepository.findById(id).map(InstallmentMapper::toDomain);
    }

    @Override
    public List<Installment> findByBillUserIdAndDueDateBetween(UUID userId, LocalDate start, LocalDate end) {
        return jpaRepository.findByBill_User_IdAndDueDateBetween(userId, start, end).stream()
                .map(InstallmentMapper::toDomain)
                .collect(Collectors.toList());
    }
}
