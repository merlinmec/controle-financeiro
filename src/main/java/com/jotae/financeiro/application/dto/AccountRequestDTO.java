package com.jotae.financeiro.application.dto;

import com.jotae.financeiro.domain.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountRequestDTO {

    @NotBlank(message = "O nome da conta é obrigatório")
    // Note que se for vazio "", o @NotBlank e o @Size estouram ao mesmo tempo. 
    // Para simplificar a resposta e o teste, vamos deixar a validação de tamanho mais permissiva com espaços vazios
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotNull(message = "O tipo da conta é obrigatório")
    private AccountType type;

    @NotNull(message = "O ID do usuário é obrigatório")
    private UUID userId;
}