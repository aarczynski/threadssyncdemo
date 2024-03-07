package pl.lunasoftware.demo.threadssync.bank;

import java.math.BigDecimal;
import java.util.Objects;

class Account {
    private final String accountNumber;
    private final BigDecimal balance;

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    Account receive(BigDecimal amount) {
        return new Account(accountNumber, balance.add(amount));
    }

    Account withdraw(BigDecimal amount) {
        return new Account(accountNumber, balance.subtract(amount));
    }

    String getAccountNumber() {
        return accountNumber;
    }

    BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
