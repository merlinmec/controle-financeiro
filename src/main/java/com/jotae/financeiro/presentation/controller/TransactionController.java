package com.jotae.financeiro.presentation.controller;

import com.jotae.financeiro.application.dto.TransactionRequestDTO;
import com.jotae.financeiro.application.dto.TransactionResponseDTO;
import com.jotae.financeiro.application.usecase.CreateTransactionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO response = createTransactionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
