package com.jotae.financeiro.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Bill {
    private final UUID id;
    private final User user;
    private final String description;
    private final Account account;
}
