package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.InstallmentResponseDTO;
import com.jotae.financeiro.application.dto.PayInstallmentRequestDTO;
import com.jotae.financeiro.domain.model.*;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayInstallmentUseCase {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final InstallmentRepository installmentRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public InstallmentResponseDTO execute(UUID installmentId, PayInstallmentRequestDTO request) {
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new IllegalArgumentException("Parcela não encontrada"));

        if (!installment.getBill().getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Esta parcela não pertence ao usuário fornecido.");
        }

        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        if (!sourceAccount.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("A conta de origem não pertence ao usuário fornecido.");
        }

        installment.checkIfPayable();

        Account destinationAccount = installment.getBill().getAccount();
        
        String description = String.format("Pagamento de Parcela %d/%d - %s", 
                installment.getNumber(), 
                installment.getTotalInstallments(), 
                installment.getBill().getDescription());

        Transaction paymentTransaction = Transaction.create(
                user,
                description,
                request.getPaymentDate(),
                Arrays.asList(
                        JournalEntry.debit(destinationAccount, installment.getAmount()),
                        JournalEntry.credit(sourceAccount, installment.getAmount())
                )
        );
        
        paymentTransaction = transactionRepository.save(paymentTransaction);
        
        installment.markAsPaid(paymentTransaction);
        Installment savedInstallment = installmentRepository.save(installment);

        return InstallmentResponseDTO.builder()
                .id(savedInstallment.getId())
                .number(savedInstallment.getNumber())
                .totalInstallments(savedInstallment.getTotalInstallments())
                .amount(savedInstallment.getAmount().getAmount())
                .dueDate(savedInstallment.getDueDate())
                .status(savedInstallment.getStatus())
                .paymentTransactionId(paymentTransaction.getId())
                .build();
    }
}
