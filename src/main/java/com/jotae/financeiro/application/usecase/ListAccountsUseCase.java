package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListAccountsUseCase {

    private final AccountRepository accountRepository;

    public List<AccountResponseDTO> execute() {
        return accountRepository.findAll().stream()
                .map(account -> AccountResponseDTO.builder()
                        .id(account.getId())
                        .name(account.getName())
                        .type(account.getType())
                        .build())
                .collect(Collectors.toList());
    }
}
