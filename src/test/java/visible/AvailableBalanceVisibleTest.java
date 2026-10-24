package visible;

import com.worksim.corebank.domain.Account;
import com.worksim.corebank.service.AccountService;
import com.worksim.corebank.store.BankStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailableBalanceVisibleTest {

    @Test
    void anActiveHoldReducesWhatTheCustomerCanSpend() {
        BankStore store = new BankStore();
        store.putAccount(new Account("acct-1", 500_00L));
        AccountService service = new AccountService(store);

        service.placeHold("h1", "acct-1", 120_00L);

        assertEquals(380_00L, service.availableBalance("acct-1"));
    }
}
