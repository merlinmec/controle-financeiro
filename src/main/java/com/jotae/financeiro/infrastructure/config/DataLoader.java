package com.jotae.financeiro.infrastructure.config;

import com.jotae.financeiro.domain.model.Account;
import com.jotae.financeiro.domain.model.AccountType;
import com.jotae.financeiro.domain.model.JournalEntry;
import com.jotae.financeiro.domain.model.Money;
import com.jotae.financeiro.domain.model.Transaction;
import com.jotae.financeiro.domain.repository.AccountRepository;
import com.jotae.financeiro.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("--- Iniciando Teste da Arquitetura Financeira ---");

        Account nubank = Account.create("Nubank Corrente", AccountType.ASSET);
        Account alimentacao = Account.create("Despesa com Alimentação", AccountType.EXPENSE);
        Account salario = Account.create("Receita Salário", AccountType.REVENUE);

        nubank = accountRepository.save(nubank);
        alimentacao = accountRepository.save(alimentacao);
        salario = accountRepository.save(salario);
        
        log.info("Contas criadas com sucesso!");

        Money valorSalario = Money.ofBRL(new BigDecimal("5000.00"));
        
        Transaction transacaoSalario = Transaction.create(
                "Recebimento de Salário Mensal",
                LocalDateTime.now(),
                Arrays.asList(
                        JournalEntry.debit(nubank, valorSalario),
                        JournalEntry.credit(salario, valorSalario)
                )
        );
        transactionRepository.save(transacaoSalario);
        log.info("Transação de Salário registrada!");

        Money valorIfood = Money.ofBRL(new BigDecimal("85.50"));
        
        Transaction transacaoIfood = Transaction.create(
                "Ifood - Lanche",
                LocalDateTime.now(),
                Arrays.asList(
                        JournalEntry.debit(alimentacao, valorIfood),
                        JournalEntry.credit(nubank, valorIfood)
                )
        );
        transactionRepository.save(transacaoIfood);
        log.info("Transação do Ifood registrada!");

        log.info("--- Resumo do Banco de Dados ---");
        List<Transaction> savedTransactions = transactionRepository.findAll();
        
        for (Transaction t : savedTransactions) {
            log.info("Transação: {} em {}", t.getDescription(), t.getDate());
            for (JournalEntry entry : t.getEntries()) {
                String tipo = entry.getAmount().getAmount().signum() >= 0 ? "DÉBITO " : "CRÉDITO";
                log.info("   -> {} de {} na conta '{}'", 
                        tipo, 
                        entry.getAmount().getAmount().abs(),
                        entry.getAccount().getName());
            }
        }
        log.info("--- Fim do Teste ---");
    }
}
