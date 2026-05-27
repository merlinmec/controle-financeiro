package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.InstallmentResponseDTO;
import com.jotae.financeiro.application.dto.PayInstallmentRequestDTO;
import com.jotae.financeiro.domain.model.*;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import com.jotae.financeiro.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayInstallmentUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private InstallmentRepository installmentRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PayInstallmentUseCase payInstallmentUseCase;

    private User testUser;
    private Account sourceAccount;
    private Account expenseAccount;
    private Bill bill;
    private Installment installment;
    private UUID installmentId;
    private PayInstallmentRequestDTO request;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).build();
        
        sourceAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .name("Nubank")
                .type(AccountType.ASSET)
                .build();
                
        expenseAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .name("Eletrônicos")
                .type(AccountType.EXPENSE)
                .build();

        bill = Bill.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .account(expenseAccount)
                .description("Notebook")
                .build();

        installmentId = UUID.randomUUID();
        installment = Installment.createPending(
                bill,
                1,
                10,
                Money.ofBRL(new BigDecimal("100.00")),
                LocalDate.now()
        );

        request = new PayInstallmentRequestDTO();
        request.setUserId(testUser.getId());
        request.setSourceAccountId(sourceAccount.getId());
        request.setPaymentDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully pay an installment and create a transaction")
    void shouldPayInstallmentSuccessfully() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(installmentRepository.findById(installmentId)).thenReturn(Optional.of(installment));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));

        Transaction savedTransaction = mock(Transaction.class);
        when(savedTransaction.getId()).thenReturn(UUID.randomUUID());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        
        when(installmentRepository.save(any(Installment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InstallmentResponseDTO response = payInstallmentUseCase.execute(installmentId, request);

        assertNotNull(response);
        assertEquals(InstallmentStatus.PAID, response.getStatus());
        assertEquals(InstallmentStatus.PAID, installment.getStatus());
        assertNotNull(response.getPaymentTransactionId());
        
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(installmentRepository, times(1)).save(installment);
    }

    @Test
    @DisplayName("Should throw exception if installment does not belong to user")
    void shouldThrowExceptionIfInstallmentDoesNotBelongToUser() {
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        Bill anotherBill = Bill.builder().id(UUID.randomUUID()).user(anotherUser).account(expenseAccount).build();
        Installment foreignInstallment = Installment.createPending(anotherBill, 1, 1, Money.ofBRL(BigDecimal.TEN), LocalDate.now());

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(installmentRepository.findById(installmentId)).thenReturn(Optional.of(foreignInstallment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payInstallmentUseCase.execute(installmentId, request);
        });

        assertEquals("Esta parcela não pertence ao usuário fornecido.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception if source account does not belong to user")
    void shouldThrowExceptionIfSourceAccountDoesNotBelongToUser() {
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        Account foreignAccount = Account.builder().id(UUID.randomUUID()).user(anotherUser).build();
        request.setSourceAccountId(foreignAccount.getId());

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(installmentRepository.findById(installmentId)).thenReturn(Optional.of(installment));
        when(accountRepository.findById(foreignAccount.getId())).thenReturn(Optional.of(foreignAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payInstallmentUseCase.execute(installmentId, request);
        });

        assertEquals("A conta de origem não pertence ao usuário fornecido.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when paying an already paid installment")
    void shouldThrowExceptionWhenPayingAlreadyPaidInstallment() {
        Transaction dummyTransaction = mock(Transaction.class);
        installment.markAsPaid(dummyTransaction); // Mark it as paid before testing

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(installmentRepository.findById(installmentId)).thenReturn(Optional.of(installment));
        when(accountRepository.findById(sourceAccount.getId())).thenReturn(Optional.of(sourceAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            payInstallmentUseCase.execute(installmentId, request);
        });

        assertEquals("This installment is already paid.", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }
}
