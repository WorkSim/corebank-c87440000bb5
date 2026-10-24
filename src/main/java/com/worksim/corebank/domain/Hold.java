package com.worksim.corebank.domain;

public record Hold(String id, String accountId, long amountCents, HoldStatus status) {
    public Hold withStatus(HoldStatus newStatus) {
        return new Hold(id, accountId, amountCents, newStatus);
    }
}
