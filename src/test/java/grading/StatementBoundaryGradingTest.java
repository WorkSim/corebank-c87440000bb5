package grading;

import com.worksim.corebank.domain.Posting;
import com.worksim.corebank.service.StatementService;
import com.worksim.corebank.store.BankStore;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** CORE-708 — statement periods are half-open: [periodStart, nextPeriodStart). */
class StatementBoundaryGradingTest {

    private static final LocalDate MARCH_1 = LocalDate.of(2026, 3, 1);
    private static final LocalDate APRIL_1 = LocalDate.of(2026, 4, 1);
    private static final LocalDate MAY_1 = LocalDate.of(2026, 5, 1);

    private BankStore seeded() {
        BankStore store = new BankStore();
        store.addPosting(new Posting("p-feb", "acct-1", -10_00L, LocalDate.of(2026, 2, 28)));
        store.addPosting(new Posting("p-mar-first", "acct-1", -20_00L, MARCH_1));
        store.addPosting(new Posting("p-mar-mid", "acct-1", -30_00L, LocalDate.of(2026, 3, 15)));
        store.addPosting(new Posting("p-boundary", "acct-1", -40_00L, APRIL_1));
        return store;
    }

    @Test
    void aStatementCoversOnlyItsOwnHalfOpenPeriod() {
        StatementService service = new StatementService(seeded());

        List<String> march = service.statement("acct-1", MARCH_1, APRIL_1).stream().map(Posting::id).toList();

        assertEquals(List.of("p-mar-first", "p-mar-mid"), march,
                "the period start is included and the next period's start is excluded");
    }

    @Test
    void aPostingOnTheBoundaryAppearsOnExactlyOneStatement() {
        StatementService service = new StatementService(seeded());

        List<Posting> march = service.statement("acct-1", MARCH_1, APRIL_1);
        List<Posting> april = service.statement("acct-1", APRIL_1, MAY_1);

        long inMarch = march.stream().filter(p -> p.id().equals("p-boundary")).count();
        long inApril = april.stream().filter(p -> p.id().equals("p-boundary")).count();

        assertEquals(1, inMarch + inApril,
                "a posting dated exactly on a period boundary must appear on one statement, not zero and not two");
        assertEquals(1, inApril, "the boundary posting belongs to the period it starts, not the one it closes");
    }

    @Test
    void postingsAreOrderedByPostDate() {
        BankStore store = new BankStore();
        store.addPosting(new Posting("late", "acct-1", -10_00L, LocalDate.of(2026, 3, 20)));
        store.addPosting(new Posting("early", "acct-1", -10_00L, LocalDate.of(2026, 3, 2)));
        store.addPosting(new Posting("middle", "acct-1", -10_00L, LocalDate.of(2026, 3, 11)));

        List<String> ids = new StatementService(store)
                .statement("acct-1", MARCH_1, APRIL_1).stream().map(Posting::id).toList();

        assertEquals(List.of("early", "middle", "late"), ids);
    }
}
