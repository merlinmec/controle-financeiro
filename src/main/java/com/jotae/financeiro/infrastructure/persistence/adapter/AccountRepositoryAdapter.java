package com.jotae.financeiro.infrastructure.persistence.adapter;

import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.infrastructure.persistence.entity.AccountEntity;
import com.jotae.financeiro.infrastructure.persistence.mapper.AccountMapper;
import com.jotae.financeiro.infrastructure.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountMapper.toEntity(account);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return AccountMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream()
                .map(AccountMapper::toDomain)
                .collect(Collectors.toList());
    }
}
