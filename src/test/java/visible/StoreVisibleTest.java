package visible;

import com.worksim.corebank.domain.*;
import com.worksim.corebank.store.BankStore;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StoreVisibleTest {

    @Test
    void storesAndReadsBackAnAccount() {
        BankStore store = new BankStore();
        store.putAccount(new Account("acct-1", 250_00L));
        assertEquals(250_00L, store.account("acct-1").ledgerBalanceCents());
    }

    @Test
    void holdsAndPostingsAreScopedToTheirAccount() {
        BankStore store = new BankStore();
        store.putHold(new Hold("h1", "acct-1", 10_00L, HoldStatus.ACTIVE));
        store.putHold(new Hold("h2", "acct-2", 20_00L, HoldStatus.ACTIVE));
        store.addPosting(new Posting("p1", "acct-1", -5_00L, LocalDate.of(2026, 1, 5)));

        assertEquals(1, store.holdsFor("acct-1").size());
        assertEquals(1, store.postingsFor("acct-1").size());
        assertEquals(0, store.postingsFor("acct-2").size());
    }

    @Test
    void withStatusLeavesTheOriginalUntouched() {
        Hold active = new Hold("h1", "acct-1", 10_00L, HoldStatus.ACTIVE);
        Hold settled = active.withStatus(HoldStatus.SETTLED);
        assertEquals(HoldStatus.ACTIVE, active.status());
        assertEquals(HoldStatus.SETTLED, settled.status());
        assertEquals(active.amountCents(), settled.amountCents());
    }
}
