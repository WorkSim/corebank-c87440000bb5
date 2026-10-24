package com.worksim.corebank.domain;

public enum HoldStatus {
    /** Earmarks funds; counts against available balance. */
    ACTIVE,
    /** Cancelled before settling; no longer earmarks anything. */
    RELEASED,
    /** Became a posted transaction; the posting now carries the money. */
    SETTLED
}
