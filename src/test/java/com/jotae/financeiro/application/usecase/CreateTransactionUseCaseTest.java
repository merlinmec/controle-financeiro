package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.TransactionRequestDTO;
import com.jotae.financeiro.application.dto.TransactionResponseDTO;
import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.AccountType;
import com.jotae.financeiro.domain.model.Transaction;
import com.jotae.financeiro.domain.model.User;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CreateTransactionUseCase createTransactionUseCase;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private User testUser;
    private Account sourceAccount;
    private Account destinationAccount;
    private TransactionRequestDTO request;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).build();
        
        sourceAccount = Account.builder().id(UUID.randomUUID()).user(testUser).name("Source").type(AccountType.ASSET).build();
        destinationAccount = Account.builder().id(UUID.randomUUID()).user(testUser).name("Destination").type(AccountType.EXPENSE).build();

        request = new TransactionRequestDTO();
        request.setUserId(testUser.getId());
        request.setSourceAccountId(sourceAccount.getId());
        request.setDestinationAccountId(destinationAccount.getId());
        request.setDescription("Test Transaction");
        request.setAmount(new BigDecimal("50.00"));
        request.setDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create a transaction successfully")
    void shouldCreateTransactionSuccessfully() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccount.getId())).thenReturn(Optional.of(destinationAccount));
        
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponseDTO response = createTransactionUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Transaction", response.getDescription());
        assertEquals(new BigDecimal("50.00"), response.getAmount());

        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction capturedTransaction = transactionCaptor.getValue();
        
        assertEquals(2, capturedTransaction.getEntries().size(), "Should have exactly one debit and one credit");
    }

    @Test
    @DisplayName("Should throw exception when source account is not found")
    void shouldThrowExceptionWhenSourceAccountIsNotFound() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createTransactionUseCase.execute(request);
        });

        assertEquals("Conta de origem não encontrada", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }
}
