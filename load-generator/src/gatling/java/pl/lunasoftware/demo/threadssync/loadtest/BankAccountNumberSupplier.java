package pl.lunasoftware.demo.threadssync.loadtest;

import io.gatling.http.client.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BankAccountNumberSupplier {
    private final List<String> bankAccounts = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private final Random random = new Random();

    public Pair<String, String> getRandomBankAccountPair() {
        List<String> bankAccountsCopy = new ArrayList<>(bankAccounts);
        String from = bankAccountsCopy.remove(random.nextInt(bankAccountsCopy.size()));
        String to = bankAccountsCopy.get(random.nextInt(bankAccountsCopy.size()));
        return new Pair<>(from, to);
    }
}
