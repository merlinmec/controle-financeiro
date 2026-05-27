package com.jotae.financeiro.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jotae.financeiro.application.dto.BillRequestDTO;
import com.jotae.financeiro.application.usecase.CreateBillUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillController.class)
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateBillUseCase createBillUseCase;

    @Test
    @DisplayName("Should return 400 Bad Request when amount is zero or negative")
    void shouldReturn400WhenAmountIsInvalid() throws Exception {
        // Arrange
        BillRequestDTO request = new BillRequestDTO();
        request.setUserId(UUID.randomUUID());
        request.setAccountId(UUID.randomUUID());
        request.setDescription("Compra");
        request.setAmount(new BigDecimal("0.00")); // Invalid amount
        request.setTotalInstallments(1);
        request.setFirstDueDate(LocalDate.now());

        // Act & Assert
        mockMvc.perform(post("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("O valor deve ser maior que zero"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when installments is less than 1")
    void shouldReturn400WhenInstallmentsInvalid() throws Exception {
        // Arrange
        BillRequestDTO request = new BillRequestDTO();
        request.setUserId(UUID.randomUUID());
        request.setAccountId(UUID.randomUUID());
        request.setDescription("Compra");
        request.setAmount(new BigDecimal("100.00"));
        request.setTotalInstallments(0); // Invalid installments
        request.setFirstDueDate(LocalDate.now());

        // Act & Assert
        mockMvc.perform(post("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.totalInstallments").value("O número de parcelas deve ser pelo menos 1"));
    }
}
