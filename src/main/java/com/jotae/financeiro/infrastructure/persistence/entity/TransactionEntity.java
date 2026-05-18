package com.jotae.financeiro.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    // Relacionamento Um-Para-Muitos: Uma transação tem vários lançamentos
    // Cascade ALL significa que ao salvar a Transação, salva os lançamentos juntos
    // orphanRemoval = true garante que se tirar um lançamento da lista, ele apaga do banco
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JournalEntryEntity> entries = new ArrayList<>();

    public TransactionEntity() {
    }
}
