package com.jotae.financeiro.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotae.financeiro.application.dto.AccountRequestDTO;
import com.jotae.financeiro.application.dto.AccountResponseDTO;
import com.jotae.financeiro.application.usecase.CreateAccountUseCase;
import com.jotae.financeiro.application.usecase.ListAccountsUseCase;
import com.jotae.financeiro.domain.model.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateAccountUseCase createAccountUseCase;

    @MockBean
    private ListAccountsUseCase listAccountsUseCase;

    @Test
    @DisplayName("Should return 200 OK and empty list when no accounts exist")
    void shouldReturnEmptyList() throws Exception {
        when(listAccountsUseCase.execute()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should return 201 Created when creating account with valid payload")
    void shouldReturn201WhenValidPayload() throws Exception {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setUserId(UUID.randomUUID());
        request.setName("Nubank");
        request.setType(AccountType.ASSET);

        AccountResponseDTO response = AccountResponseDTO.builder()
                .id(UUID.randomUUID())
                .name("Nubank")
                .type(AccountType.ASSET)
                .build();

        when(createAccountUseCase.execute(any(AccountRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nubank"))
                .andExpect(jsonPath("$.type").value("ASSET"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        request.setUserId(UUID.randomUUID());
        // null makes @NotBlank trigger its message instead of @Size
        request.setName(null);
        request.setType(AccountType.ASSET);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("O nome da conta é obrigatório"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when userId is null")
    void shouldReturn400WhenUserIdIsNull() throws Exception {
        // Arrange
        AccountRequestDTO request = new AccountRequestDTO();
        // request.setUserId(null); // Missing User ID
        request.setName("Nubank");
        request.setType(AccountType.ASSET);

        // Act & Assert
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("O ID do usuário é obrigatório"));
    }
}
