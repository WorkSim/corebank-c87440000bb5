package com.worksim.corebank.domain;

public record Account(String id, long ledgerBalanceCents) {
    public Account withLedgerBalanceCents(long cents) {
        return new Account(id, cents);
    }
}
