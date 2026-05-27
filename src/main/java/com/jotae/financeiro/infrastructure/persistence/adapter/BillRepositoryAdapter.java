package com.jotae.financeiro.infrastructure.persistence.adapter;

import com.jotae.financeiro.domain.model.Bill;
import com.jotae.financeiro.domain.repository.BillRepository;
import com.jotae.financeiro.infrastructure.persistence.mapper.BillMapper;
import com.jotae.financeiro.infrastructure.persistence.repository.BillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BillRepositoryAdapter implements BillRepository {

    private final BillJpaRepository jpaRepository;

    @Override
    public Bill save(Bill bill) {
        var entity = BillMapper.toEntity(bill);
        return BillMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Bill> findById(UUID id) {
        return jpaRepository.findById(id).map(BillMapper::toDomain);
    }

    @Override
    public List<Bill> findByUserId(UUID userId) {
        return jpaRepository.findByUser_Id(userId).stream()
                .map(BillMapper::toDomain)
                .collect(Collectors.toList());
    }
}
