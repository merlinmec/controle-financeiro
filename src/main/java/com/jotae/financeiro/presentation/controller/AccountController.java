package com.jotae.financeiro.presentation.controller;

import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.application.usecase.ListAccountsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final ListAccountsUseCase listAccountsUseCase;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> listAll() {
        List<AccountResponseDTO> accounts = listAccountsUseCase.execute();
        return ResponseEntity.ok(accounts);
    }
}
