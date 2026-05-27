package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.AccountRequestDTO;
import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.User;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponseDTO execute(AccountRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Account account = Account.create(user, request.getName(), request.getType());
        Account savedAccount = accountRepository.save(account);

        return AccountResponseDTO.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .type(savedAccount.getType())
                .build();
    }
}