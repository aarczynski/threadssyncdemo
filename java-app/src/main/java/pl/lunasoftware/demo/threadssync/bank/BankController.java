package pl.lunasoftware.demo.threadssync.bank;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lunasoftware.demo.threadssync.bank.dto.BalanceDto;
import pl.lunasoftware.demo.threadssync.bank.dto.TransferRequestDto;

@RestController
@RequestMapping("/api/v1")
public class BankController {

    private final Bank bank;

    public BankController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping(value = "/transfers", params = "race=true")
    public void transferWithRace(@Valid @RequestBody TransferRequestDto request) {
        bank.transferWithRace(request.from(), request.to(), request.amount());
    }

    @PostMapping(value = "/transfers", params = "deadlock=true")
    public void transferWithDeadlock(@Valid @RequestBody TransferRequestDto request) {
        bank.transferWithDeadlock(request.from(), request.to(), request.amount());
    }

    @PostMapping("/transfers")
    public void transfer(@Valid @RequestBody TransferRequestDto request) {
        bank.transfer(request.from(), request.to(), request.amount());
    }

    @GetMapping("/total-assets")
    public BalanceDto getTotalAssets() {
        return bank.getTotalBalance();
    }
}
