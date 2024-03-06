package pl.lunasoftware.demo.threadssync.bank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record BalanceDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal totalBalance,
        List<AccountDto> accounts
) {
    public BalanceDto(BigDecimal totalBalance, List<AccountDto> accounts) {
        this.totalBalance = totalBalance.setScale(2, RoundingMode.HALF_UP);
        this.accounts = accounts;
    }

    public record AccountDto(
            String accountNumber,
            @JsonFormat(shape = JsonFormat.Shape.STRING)
            BigDecimal balance
    ) {
        public AccountDto(String accountNumber, BigDecimal balance) {
            this.accountNumber = accountNumber;
            this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
