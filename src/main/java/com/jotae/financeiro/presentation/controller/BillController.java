package com.jotae.financeiro.presentation.controller;

import com.jotae.financeiro.application.dto.BillRequestDTO;
import com.jotae.financeiro.application.dto.BillResponseDTO;
import com.jotae.financeiro.application.usecase.CreateBillUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final CreateBillUseCase createBillUseCase;

    @PostMapping
    public ResponseEntity<BillResponseDTO> createBill(@Valid @RequestBody BillRequestDTO request) {
        BillResponseDTO response = createBillUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
