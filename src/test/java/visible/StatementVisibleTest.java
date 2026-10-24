package visible;

import com.worksim.corebank.domain.Posting;
import com.worksim.corebank.service.StatementService;
import com.worksim.corebank.store.BankStore;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementVisibleTest {

    @Test
    void theClosingDatePostingRollsIntoTheNextStatement() {
        BankStore store = new BankStore();
        store.addPosting(new Posting("p1", "acct-1", -10_00L, LocalDate.of(2026, 3, 10)));
        store.addPosting(new Posting("p2", "acct-1", -10_00L, LocalDate.of(2026, 4, 1)));

        StatementService service = new StatementService(store);

        assertEquals(1, service.statement("acct-1", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1)).size());
    }
}
