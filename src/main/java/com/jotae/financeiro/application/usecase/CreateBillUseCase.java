package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.BillRequestDTO;
import com.jotae.financeiro.application.dto.BillResponseDTO;
import com.jotae.financeiro.domain.model.*;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.BillRepository;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CreateBillUseCase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BillRepository billRepository;
    private final InstallmentRepository installmentRepository;

    @Transactional
    public BillResponseDTO execute(BillRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta (categoria) não encontrada"));

        Bill bill = Bill.builder()
                .id(java.util.UUID.randomUUID())
                .user(user)
                .description(request.getDescription())
                .account(account)
                .build();
        
        billRepository.save(bill);

        BigDecimal installmentAmount = request.getAmount().divide(
                new BigDecimal(request.getTotalInstallments()),
                2, // scale for BRL
                RoundingMode.HALF_EVEN
        );

        List<Installment> installments = IntStream.rangeClosed(1, request.getTotalInstallments())
                .mapToObj(i -> Installment.createPending(
                        bill,
                        i,
                        request.getTotalInstallments(),
                        Money.ofBRL(installmentAmount),
                        request.getFirstDueDate().plusMonths(i - 1)
                ))
                .collect(Collectors.toList());
        
        installmentRepository.saveAll(installments);

        return BillResponseDTO.builder()
                .id(bill.getId())
                .description(bill.getDescription())
                .accountName(account.getName())
                .totalInstallments(request.getTotalInstallments())
                .build();
    }
}
