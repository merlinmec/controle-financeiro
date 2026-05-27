package com.jotae.financeiro.application.usecase;

import com.jotae.financeiro.application.dto.AccountRequestDTO;
import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.AccountType;
import com.jotae.financeiro.domain.model.User;
import com.jotae.financeiro.domain.repository.AccountRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreateAccountUseCase createAccountUseCase;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private User testUser;
    private AccountRequestDTO request;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).build();

        request = new AccountRequestDTO();
        request.setUserId(testUser.getId());
        request.setName("Nova Conta Corrente");
        request.setType(AccountType.ASSET);
    }

    @Test
    @DisplayName("Should create an account successfully")
    void shouldCreateAccountSuccessfully() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        
        // When save is called, just return the same account that was passed in
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponseDTO response = createAccountUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("Nova Conta Corrente", response.getName());
        assertEquals(AccountType.ASSET, response.getType());

        // Capture the account passed to the repository and verify its properties
        verify(accountRepository).save(accountCaptor.capture());
        Account capturedAccount = accountCaptor.getValue();
        
        assertEquals(testUser.getId(), capturedAccount.getUser().getId());
        assertEquals("Nova Conta Corrente", capturedAccount.getName());
    }

    @Test
    @DisplayName("Should throw exception when user for account creation is not found")
    void shouldThrowExceptionWhenUserIsNotFound() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createAccountUseCase.execute(request);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }
}
