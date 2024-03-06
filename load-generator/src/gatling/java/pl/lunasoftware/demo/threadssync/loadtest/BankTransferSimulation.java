package pl.lunasoftware.demo.threadssync.loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BankTransferSimulation extends Simulation {

    private static final int RPS = 50;
    private static final Duration A_MINUTE = Duration.ofSeconds(60);
    private final BankAccountNumberSupplier bankAccountNumberSupplier = new BankAccountNumberSupplier();

    public BankTransferSimulation() {
        this.setUp(employeesDataScenario()
                .injectOpen(constantUsersPerSec(RPS).during(A_MINUTE))
                .andThen(verifyTotalAssetsBalance().injectOpen(atOnceUsers(1))))
                .protocols(httpProtocolBuilder());
    }

    private ScenarioBuilder employeesDataScenario() {
        return scenario("Load Test send bank transfer")
                .feed(randomBankAccountNumbersFeeder())
                .feed(randomAmountFeeder())
                .exec(http("request bank transfer")
                        .post("/api/v1/transfers" + getSimulationModeParam())
                        .body(StringBody("""
                                {
                                    "from": "#{bankAccountNumbers.left}",
                                    "to": "#{bankAccountNumbers.right}",
                                    "amount": #{amount}
                                }
                                """))
                        .header("Content-Type", "application/json")
                        .check(status().is(200))
                );
    }

    private ScenarioBuilder verifyTotalAssetsBalance() {
        return scenario("Check total bank assets balance")
                .exec(http("request total bank assets balance")
                        .get("/api/v1/total-assets")
                        .check(status().is(200))
                        .check(jsonPath("$.totalBalance").is("10000.00"))
                );
    }

    private HttpProtocolBuilder httpProtocolBuilder() {
        return http
                .baseUrl("http://localhost:8080")
                .acceptHeader("application/json")
                .userAgentHeader("Gatling/Performance Test");
    }

    private Iterator<Map<String, Object>> randomBankAccountNumbersFeeder() {
        return Stream.generate(
                (Supplier<Map<String, Object>>) () -> Collections.singletonMap("bankAccountNumbers", bankAccountNumberSupplier.getRandomBankAccountPair())
        ).iterator();
    }

    private Iterator<Map<String, Object>> randomAmountFeeder() {
        return Stream.generate(
                (Supplier<Map<String, Object>>) () -> Collections.singletonMap("amount", new BigDecimal(ThreadLocalRandom.current().nextInt(50) + 1))
        ).iterator();
    }

    private String getSimulationModeParam() {
        if (System.getProperty("mode") == null) {
            return "";
        }
        return switch (System.getProperty("mode")) {
            case "race" -> "?race=true";
            case "deadlock" -> "?deadlock=true";
            default -> "";
        };
    }
}
