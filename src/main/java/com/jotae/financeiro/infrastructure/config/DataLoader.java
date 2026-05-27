package com.jotae.financeiro.infrastructure.config;

import com.jotae.financeiro.domain.model.*;
import com.jotae.financeiro.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BillRepository billRepository;
    private final InstallmentRepository installmentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("--- Verificando base de dados ---");

        String testEmail = "teste@email.com";
        Optional<User> existingUser = userRepository.findByEmail(testEmail);

        if (existingUser.isPresent()) {
            log.info("Usuário de teste já existe (ID: {}). Pulando carga inicial de dados.", existingUser.get().getId());
            return;
        }

        log.info("--- Iniciando Teste da Arquitetura Financeira (Carga Inicial) ---");

        User testUser = User.create("Usuário Teste", testEmail, "hashed_password");
        testUser = userRepository.save(testUser);
        log.info("Usuário de teste criado com ID: {}", testUser.getId());

        Account nubank = Account.create(testUser, "Nubank Corrente", AccountType.ASSET);
        Account alimentacao = Account.create(testUser, "Despesa com Alimentação", AccountType.EXPENSE);
        Account salario = Account.create(testUser, "Receita Salário", AccountType.REVENUE);
        Account eletronicos = Account.create(testUser, "Despesa com Eletrônicos", AccountType.EXPENSE);

        nubank = accountRepository.save(nubank);
        alimentacao = accountRepository.save(alimentacao);
        salario = accountRepository.save(salario);
        eletronicos = accountRepository.save(eletronicos);
        log.info("Contas criadas com sucesso!");

        Money valorSalario = Money.ofBRL(new BigDecimal("5000.00"));
        Transaction transacaoSalario = Transaction.create(
                testUser,
                "Recebimento de Salário Mensal",
                LocalDateTime.now(),
                Arrays.asList(
                        JournalEntry.debit(nubank, valorSalario),
                        JournalEntry.credit(salario, valorSalario)
                )
        );
        transactionRepository.save(transacaoSalario);
        log.info("Transação de Salário registrada!");

        log.info("--- Testando o motor de parcelamento ---");
        Bill compraNotebook = Bill.builder()
                .id(java.util.UUID.randomUUID())
                .user(testUser)
                .description("Compra Notebook Dell")
                .account(eletronicos) 
                .build();

        billRepository.save(compraNotebook);

        Money valorParcela = Money.ofBRL(new BigDecimal("300.00"));
        List<Installment> parcelas = IntStream.rangeClosed(1, 12)
                .mapToObj(i -> Installment.createPending(
                        compraNotebook,
                        i,
                        12,
                        valorParcela,
                        LocalDate.now().plusMonths(i - 1)
                ))
                .collect(Collectors.toList());
        
        installmentRepository.saveAll(parcelas);
        log.info("Compra parcelada do Notebook registrada com 12 parcelas de R$ 300,00.");

        // 5. Simulate paying the first installment of the notebook
        Installment primeiraParcela = parcelas.get(0);
        log.info("Pagando a primeira parcela do notebook (ID: {})", primeiraParcela.getId());

        Transaction pagamentoParcela = Transaction.create(
                testUser,
                "Pagamento da parcela 1/12 do Notebook Dell",
                LocalDateTime.now(),
                Arrays.asList(
                        // Debit the expense account
                        JournalEntry.debit(eletronicos, primeiraParcela.getAmount()),
                        // Credit the asset account (money leaves the bank)
                        JournalEntry.credit(nubank, primeiraParcela.getAmount())
                )
        );
        pagamentoParcela = transactionRepository.save(pagamentoParcela);

        primeiraParcela.markAsPaid(pagamentoParcela);
        installmentRepository.save(primeiraParcela);
        log.info("Primeira parcela paga e vinculada à transação ID: {}", pagamentoParcela.getId());

        log.info("--- Fim do Teste ---");
    }
}
