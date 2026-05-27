package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.BillRequestDTO;
import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.Installment;
import com.jotae.financeiro.domain.model.User;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.BillRepository;
import com.jotae.financeiro.domain.repository.InstallmentRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBillUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BillRepository billRepository;
    @Mock
    private InstallmentRepository installmentRepository;

    @InjectMocks
    private CreateBillUseCase createBillUseCase;

    @Captor
    private ArgumentCaptor<List<Installment>> installmentListCaptor;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).build();
        testAccount = Account.builder().id(UUID.randomUUID()).user(testUser).build();
    }

    @Test
    @DisplayName("Should create a bill with multiple installments correctly")
    void shouldCreateBillWithMultipleInstallments() {
        // Arrange
        BillRequestDTO request = new BillRequestDTO();
        request.setUserId(testUser.getId());
        request.setAccountId(testAccount.getId());
        request.setDescription("Compra de Teste");
        request.setAmount(new BigDecimal("1200.00"));
        request.setTotalInstallments(12);
        request.setFirstDueDate(LocalDate.of(2024, 1, 15));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        createBillUseCase.execute(request);

        verify(installmentRepository, times(1)).saveAll(installmentListCaptor.capture());

        List<Installment> capturedInstallments = installmentListCaptor.getValue();

        assertNotNull(capturedInstallments);
        assertEquals(12, capturedInstallments.size(), "Deveriam ter sido criadas 12 parcelas.");

        Installment firstInstallment = capturedInstallments.get(0);
        assertEquals(0, new BigDecimal("100.00").compareTo(firstInstallment.getAmount().getAmount()), "O valor da parcela deve ser 100.00.");
        assertEquals(LocalDate.of(2024, 1, 15), firstInstallment.getDueDate(), "A data da primeira parcela está incorreta.");

        Installment lastInstallment = capturedInstallments.get(11);
        assertEquals(LocalDate.of(2024, 12, 15), lastInstallment.getDueDate(), "A data da última parcela está incorreta.");
    }

    @Test
    @DisplayName("Should correctly handle rounding for amounts with periodic decimals")
    void shouldHandleRoundingForPeriodicDecimals() {
        BillRequestDTO request = new BillRequestDTO();
        request.setUserId(testUser.getId());
        request.setAccountId(testAccount.getId());
        request.setDescription("Teste de Dízima");
        request.setAmount(new BigDecimal("100.00"));
        request.setTotalInstallments(3);
        request.setFirstDueDate(LocalDate.of(2024, 1, 1));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));

        createBillUseCase.execute(request);

        verify(installmentRepository, times(1)).saveAll(installmentListCaptor.capture());
        List<Installment> capturedInstallments = installmentListCaptor.getValue();

        assertEquals(3, capturedInstallments.size());

        assertEquals(0, new BigDecimal("33.33").compareTo(capturedInstallments.get(0).getAmount().getAmount()));
        assertEquals(0, new BigDecimal("33.33").compareTo(capturedInstallments.get(1).getAmount().getAmount()));

        assertEquals(0, new BigDecimal("33.34").compareTo(capturedInstallments.get(2).getAmount().getAmount()));

        BigDecimal totalSum = capturedInstallments.stream()
                .map(inst -> inst.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        assertEquals(0, new BigDecimal("100.00").compareTo(totalSum), "A soma das parcelas deve ser exatamente igual ao valor original.");
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
        BillRequestDTO request = new BillRequestDTO();
        request.setUserId(UUID.randomUUID());

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> createBillUseCase.execute(request));

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(billRepository, never()).save(any());
    }
}
