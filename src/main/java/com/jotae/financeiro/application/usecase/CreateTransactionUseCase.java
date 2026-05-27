package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.TransactionRequestDTO;
import com.jotae.financeiro.application.dto.TransactionResponseDTO;
import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.JournalEntry;
import com.jotae.financeiro.domain.model.Money;
import com.jotae.financeiro.domain.model.Transaction;
import com.jotae.financeiro.domain.model.User;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Caso de Uso (Application Layer) para criar uma nova transação.
 */
@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponseDTO execute(TransactionRequestDTO request) {
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));
                
        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        Money moneyAmount = Money.ofBRL(request.getAmount());

        Transaction transaction = Transaction.create(
                user,
                request.getDescription(),
                request.getDate(),
                Arrays.asList(
                        JournalEntry.credit(sourceAccount, moneyAmount),
                        JournalEntry.debit(destinationAccount, moneyAmount)
                )
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponseDTO.builder()
                .id(savedTransaction.getId())
                .description(savedTransaction.getDescription())
                .date(savedTransaction.getDate())
                .amount(request.getAmount()) 
                .sourceAccountName(sourceAccount.getName())
                .destinationAccountName(destinationAccount.getName())
                .build();
    }
}
