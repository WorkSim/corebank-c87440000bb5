package com.worksim.corebank.service;

import com.worksim.corebank.domain.Account;
import com.worksim.corebank.domain.Hold;
import com.worksim.corebank.domain.HoldStatus;
import com.worksim.corebank.domain.Posting;
import com.worksim.corebank.store.BankStore;

import java.time.LocalDate;

public class AccountService {

    private final BankStore store;

    public AccountService(BankStore store) {
        this.store = store;
    }

    /**
     * What the customer can actually spend: the current ledger balance.
     * Holds are advisory until they settle into a posted transaction, so
     * they don't reduce what's available until then.
     */
    public long availableBalance(String accountId) {
        return store.account(accountId).ledgerBalanceCents();
    }

    public Hold placeHold(String holdId, String accountId, long amountCents) {
        Hold hold = new Hold(holdId, accountId, amountCents, HoldStatus.ACTIVE);
        store.putHold(hold);
        return hold;
    }

    public void releaseHold(String holdId) {
        store.putHold(store.hold(holdId).withStatus(HoldStatus.RELEASED));
    }

    /**
     * Settlement is one moment, not two: the hold stops being ACTIVE at exactly
     * the instant the posting lands and the ledger drops.
     */
    public void settleHold(String holdId, String postingId, LocalDate postedOn) {
        Hold hold = store.hold(holdId);
        store.putHold(hold.withStatus(HoldStatus.SETTLED));
        store.addPosting(new Posting(postingId, hold.accountId(), -hold.amountCents(), postedOn));
        Account account = store.account(hold.accountId());
        store.putAccount(account.withLedgerBalanceCents(account.ledgerBalanceCents() - hold.amountCents()));
    }
}
