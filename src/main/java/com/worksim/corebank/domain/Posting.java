package com.worksim.corebank.domain;

import java.time.LocalDate;

/** A posted transaction. Debits are negative, credits positive. */
public record Posting(String id, String accountId, long amountCents, LocalDate postedOn) {}
