package grading;

import com.worksim.corebank.domain.Account;
import com.worksim.corebank.service.AccountService;
import com.worksim.corebank.store.BankStore;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** CORE-701 — available balance must account for pending holds. */
class AvailableBalanceGradingTest {

    private BankStore seeded() {
        BankStore store = new BankStore();
        store.putAccount(new Account("acct-1", 1_000_00L));
        return store;
    }

    @Test
    void availableBalanceIsLedgerMinusActiveHolds() {
        BankStore store = seeded();
        AccountService service = new AccountService(store);
        service.placeHold("h1", "acct-1", 150_00L);
        service.placeHold("h2", "acct-1", 50_00L);

        assertEquals(800_00L, service.availableBalance("acct-1"),
                "available balance must be ledger balance minus the sum of active holds");
    }

    @Test
    void placingAHoldReducesAvailableAndReleasingRestoresIt() {
        BankStore store = seeded();
        AccountService service = new AccountService(store);

        assertEquals(1_000_00L, service.availableBalance("acct-1"));
        service.placeHold("h1", "acct-1", 200_00L);
        assertEquals(800_00L, service.availableBalance("acct-1"), "placing a hold must reduce available balance");
        service.releaseHold("h1");
        assertEquals(1_000_00L, service.availableBalance("acct-1"), "releasing a hold must restore available balance");
    }

    @Test
    void aSettledHoldIsNotCountedTwice() {
        BankStore store = seeded();
        AccountService service = new AccountService(store);
        service.placeHold("h1", "acct-1", 200_00L);

        service.settleHold("h1", "p1", LocalDate.of(2026, 3, 14));

        // The money left the ledger once. If the settled hold still counted as
        // active, available balance would be 600_00 — charged twice.
        assertEquals(800_00L, service.availableBalance("acct-1"),
                "a hold that settled into a posted transaction must not also count as an active hold");
    }
}
