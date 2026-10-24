package com.worksim.corebank.service;

import com.worksim.corebank.domain.Posting;
import com.worksim.corebank.store.BankStore;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class StatementService {

    private final BankStore store;

    public StatementService(BankStore store) {
        this.store = store;
    }

    /**
     * Inclusive on both ends: a statement covers every posting from
     * periodStart through nextPeriodStart, so nothing dated exactly on
     * either boundary is left off.
     *
     * Ties break on id so equal-dated postings have a stable order.
     */
    public List<Posting> statement(String accountId, LocalDate periodStart, LocalDate nextPeriodStart) {
        return store.postingsFor(accountId).stream()
                .filter(posting -> !posting.postedOn().isBefore(periodStart))
                .filter(posting -> !posting.postedOn().isAfter(nextPeriodStart))
                .sorted(Comparator.comparing(Posting::postedOn).thenComparing(Posting::id))
                .toList();
    }
}
