package pl.lunasoftware.demo.threadssync.bank;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountRepository {
    private final List<Account> accounts;

    AccountRepository() {
        this.accounts = new ArrayList<>(List.of(
                new Account("1", BigDecimal.valueOf(1000)),
                new Account("2", BigDecimal.valueOf(1000)),
                new Account("3", BigDecimal.valueOf(1000)),
                new Account("4", BigDecimal.valueOf(1000)),
                new Account("5", BigDecimal.valueOf(1000)),
                new Account("6", BigDecimal.valueOf(1000)),
                new Account("7", BigDecimal.valueOf(1000)),
                new Account("8", BigDecimal.valueOf(1000)),
                new Account("9", BigDecimal.valueOf(1000)),
                new Account("10", BigDecimal.valueOf(1000))
        ));
    }

    List<Account> findAll() {
        simulateDbAccessDelay();
        return accounts;
    }

    Optional<Account> findByAccountNumber(String accountNumber) {
        simulateDbAccessDelay();
        return accounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    Account save(Account account) {
        simulateDbAccessDelay();
        accounts.set(accounts.indexOf(account), account);
        return account;
    }

    private static void simulateDbAccessDelay() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10) + 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
