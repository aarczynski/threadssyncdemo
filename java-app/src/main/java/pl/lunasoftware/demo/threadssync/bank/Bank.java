package pl.lunasoftware.demo.threadssync.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.lunasoftware.demo.threadssync.bank.dto.BalanceDto;

import java.math.BigDecimal;
import java.util.List;

@Component
public class Bank {
    private static final Logger log = LoggerFactory.getLogger(Bank.class);

    private final AccountRepository accountRepository;

    public Bank(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void transferWithRace(String from, String to, BigDecimal amount) {
        log.info("Transfer of {} from account {} to account {} requested", amount, from, to);
        Account fromAccount = findAccount(from);
        Account toAccount = findAccount(to);
        accountRepository.save(fromAccount.withdraw(amount));
        accountRepository.save(toAccount.receive(amount));
        log.info("Transferred {} from account {} to account {}", amount, from, to);
    }

    public void slowTransfer(String from, String to, BigDecimal amount) {
        log.info("Transfer of {} from account {} to account {} requested", amount, from, to);
        synchronized (this) {
            Account fromAccount = findAccount(from);
            Account toAccount = findAccount(to);
            accountRepository.save(fromAccount.withdraw(amount));
            accountRepository.save(toAccount.receive(amount));
            log.info("Transferred {} from account {} to account {}", amount, from, to);
        }
    }

    public void transferWithDeadlock(String from, String to, BigDecimal amount) {
        log.info("Transfer of {} from account {} to account {} requested", amount, from, to);
        synchronized (from.intern()) {
            synchronized (to.intern()) {
                Account fromAccount = findAccount(from);
                Account toAccount = findAccount(to);
                accountRepository.save(fromAccount.withdraw(amount));
                accountRepository.save(toAccount.receive(amount));
                log.info("Transferred {} from account {} to account {}", amount, from, to);
            }
        }
    }

    public void transfer(String from, String to, BigDecimal amount) {
        log.info("Transfer of {} from account {} to account {} requested", amount, from, to);
        String lock1 = from.compareTo(to) < 0 ? from.intern() : to.intern();
        String lock2 = from.compareTo(to) >= 0 ? from.intern() : to.intern();
        synchronized (lock1.intern()) {
            synchronized (lock2.intern()) {
                Account fromAccount = findAccount(from);
                Account toAccount = findAccount(to);
                accountRepository.save(fromAccount.withdraw(amount));
                accountRepository.save(toAccount.receive(amount));
                log.info("Transferred {} from account {} to account {}", amount, from, to);
            }
        }
    }

    public BalanceDto getTotalBalance() {
        List<Account> accounts = accountRepository.findAll();

        BigDecimal total = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<BalanceDto.AccountDto> accountDtos = accounts.stream()
                .map(a -> new BalanceDto.AccountDto(a.getAccountNumber(), a.getBalance()))
                .toList();

        return new BalanceDto(total, accountDtos);
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
