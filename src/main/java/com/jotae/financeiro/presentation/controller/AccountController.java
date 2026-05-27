package com.jotae.financeiro.presentation.controller;

import com.jotae.financeiro.application.dto.AccountRequestDTO;
import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.application.usecase.CreateAccountUseCase;
import com.jotae.financeiro.application.usecase.ListAccountsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final ListAccountsUseCase listAccountsUseCase;
    private final CreateAccountUseCase createAccountUseCase;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> listAll() {
        List<AccountResponseDTO> accounts = listAccountsUseCase.execute();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        AccountResponseDTO response = createAccountUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}