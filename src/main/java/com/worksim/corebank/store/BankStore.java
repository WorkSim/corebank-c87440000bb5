package com.worksim.corebank.store;

import com.worksim.corebank.domain.Account;
import com.worksim.corebank.domain.Hold;
import com.worksim.corebank.domain.Posting;

import java.util.*;

/**
 * In-memory storage — the only mutable state in the app. Insertion-ordered so
 * results are deterministic without a database.
 */
public class BankStore {
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final Map<String, Hold> holds = new LinkedHashMap<>();
    private final List<Posting> postings = new ArrayList<>();

    public void putAccount(Account account) {
        accounts.put(account.id(), account);
    }

    public Account account(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) throw new IllegalArgumentException("No such account: " + accountId);
        return account;
    }

    public void putHold(Hold hold) {
        holds.put(hold.id(), hold);
    }

    public Hold hold(String holdId) {
        Hold hold = holds.get(holdId);
        if (hold == null) throw new IllegalArgumentException("No such hold: " + holdId);
        return hold;
    }

    public List<Hold> holdsFor(String accountId) {
        return holds.values().stream().filter(h -> h.accountId().equals(accountId)).toList();
    }

    public void addPosting(Posting posting) {
        postings.add(posting);
    }

    public List<Posting> postingsFor(String accountId) {
        return postings.stream().filter(p -> p.accountId().equals(accountId)).toList();
    }
}
