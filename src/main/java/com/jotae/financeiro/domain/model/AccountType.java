package com.jotae.financeiro.domain.model;

public enum AccountType {
    ASSET,      // Ativos: Conta Corrente, Dinheiro em Carteira, Investimentos
    LIABILITY,  // Passivos: Fatura de Cartão de Crédito, Empréstimos a pagar
    EQUITY,     // Patrimônio Líquido: Capital inicial
    REVENUE,    // Receitas: Salários, Rendimentos (De onde vem o dinheiro para o sistema)
    EXPENSE     // Despesas: Alimentação, Moradia, Transporte (Para onde vai o dinheiro)
}
