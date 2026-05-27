package com.jotae.financeiro.presentation.controller;

import com.jotae.financeiro.application.dto.InstallmentResponseDTO;
import com.jotae.financeiro.application.dto.PayInstallmentRequestDTO;
import com.jotae.financeiro.application.usecase.ListInstallmentsUseCase;
import com.jotae.financeiro.application.usecase.PayInstallmentUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final PayInstallmentUseCase payInstallmentUseCase;
    private final ListInstallmentsUseCase listInstallmentsUseCase;

    @PostMapping("/{id}/pay")
    public ResponseEntity<InstallmentResponseDTO> payInstallment(
            @PathVariable("id") UUID installmentId,
            @Valid @RequestBody PayInstallmentRequestDTO request) {
        
        InstallmentResponseDTO response = payInstallmentUseCase.execute(installmentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InstallmentResponseDTO>> listInstallments(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<InstallmentResponseDTO> response = listInstallmentsUseCase.execute(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
