package com.worksim.corebank;

import com.worksim.corebank.domain.Account;
import com.worksim.corebank.service.AccountService;
import com.worksim.corebank.service.InterestCalculator;
import com.worksim.corebank.service.StatementService;
import com.worksim.corebank.store.BankStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CorebankApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorebankApplication.class, args);
    }

    /** Seeded in-memory data so the app is explorable without a database. */
    @Bean
    BankStore bankStore() {
        BankStore store = new BankStore();
        store.putAccount(new Account("acct-1", 2_500_00L));
        return store;
    }

    @Bean
    AccountService accountService(BankStore store) {
        return new AccountService(store);
    }

    @Bean
    StatementService statementService(BankStore store) {
        return new StatementService(store);
    }

    @Bean
    InterestCalculator interestCalculator() {
        return new InterestCalculator();
    }
}
