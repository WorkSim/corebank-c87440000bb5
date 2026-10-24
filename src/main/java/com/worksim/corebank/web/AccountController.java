package com.worksim.corebank.web;

import com.worksim.corebank.domain.Posting;
import com.worksim.corebank.service.AccountService;
import com.worksim.corebank.service.InterestCalculator;
import com.worksim.corebank.service.StatementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Thin HTTP layer — every decision lives in the services. */
@RestController
@RequestMapping("/accounts/{accountId}")
public class AccountController {

    private final AccountService accounts;
    private final StatementService statements;
    private final InterestCalculator interest;

    public AccountController(AccountService accounts, StatementService statements, InterestCalculator interest) {
        this.accounts = accounts;
        this.statements = statements;
        this.interest = interest;
    }

    @GetMapping("/available-balance")
    public Map<String, Object> availableBalance(@PathVariable String accountId) {
        return Map.of("accountId", accountId, "availableBalanceCents", accounts.availableBalance(accountId));
    }

    @GetMapping("/statement")
    public List<Posting> statement(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextPeriodStart) {
        return statements.statement(accountId, periodStart, nextPeriodStart);
    }

    @GetMapping("/interest")
    public Map<String, Object> interest(
            @PathVariable String accountId,
            @RequestParam long principalCents,
            @RequestParam BigDecimal annualRate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return Map.of("accruedCents", interest.accrueCents(principalCents, annualRate, from, to));
    }
}
