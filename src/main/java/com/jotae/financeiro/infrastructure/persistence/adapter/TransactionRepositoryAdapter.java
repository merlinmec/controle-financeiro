package com.jotae.financeiro.infrastructure.persistence.adapter;

import com.jotae.financeiro.domain.model.Transaction;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import com.jotae.financeiro.infrastructure.persistence.entity.TransactionEntity;
import com.jotae.financeiro.infrastructure.persistence.mapper.TransactionMapper;
import com.jotae.financeiro.infrastructure.persistence.repository.TransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaRepository.save(entity);
        return TransactionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(TransactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
