package pl.lunasoftware.demo.threadssync.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotBlank(message = "Source account number is mandatory")
        String from,
        @NotBlank(message = "Destination account number is mandatory")
        String to,
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount
) {

}
