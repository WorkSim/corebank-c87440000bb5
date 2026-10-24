package grading;

import com.worksim.corebank.service.InterestCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** CORE-715 — accrual follows the actual day count, not a 30-day month. */
class InterestAccrualGradingTest {

    private static final long PRINCIPAL = 10_000_00L;      // $10,000
    private static final BigDecimal RATE = new BigDecimal("0.05"); // 5% per year
    private final InterestCalculator calculator = new InterestCalculator();

    @Test
    void aFullYearAccruesExactlyThePrincipalTimesTheRate() {
        long accrued = calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        // 5% of $10,000 is $500 — for the whole year, whatever its length. A
        // 30-day-month basis inflates this by 365/360.
        assertEquals(500_00L, accrued, "a full year must accrue exactly principal x rate");
    }

    @Test
    void longerMonthsAccrueMoreThanShorterOnes() {
        long january = calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));   // 31 days
        long february = calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));   // 28 days

        // January 2026: 50,000 / 365 × 31 = 4246.575... → rounds to 4247 cents.
        // A 360-day-year defect would yield 50,000 / 360 × 31 = 4305.55... → 4306.
        // This exact value catches the CORE-715 defect; a naive 30/30 defect would equal both.
        assertEquals(4247L, january,
                "January 2026 (31 days) must accrue exactly 4247 cents via actual/actual");

        assertTrue(january > february,
                "a 31-day month must accrue more than a 28-day month; got " + january + " vs " + february);
    }

    @Test
    void leapYearFebruaryAccruesItsExtraDay() {
        long leap = calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2024, 2, 1), LocalDate.of(2024, 3, 1));    // 29 days
        long nonLeap = calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 1));    // 28 days

        assertTrue(leap > nonLeap,
                "February 2024 has 29 days and must accrue more than February 2026's 28; got " + leap + " vs " + nonLeap);
    }

    @Test
    void anEmptyPeriodAccruesNothing() {
        assertEquals(0L, calculator.accrueCents(PRINCIPAL, RATE,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1)));
    }

    @Test
    void roundingOncePerPeriodNotPerDay() {
        // $1.00 at 5% per year accrues exactly 5 cents over a full year.
        // Each day's accrual: 100 × 0.05 / 365 ≈ 0.0137 cents.
        // Per-day rounding (0.0137 → 0) would accumulate to 0 total.
        // Per-period rounding (365 × 0.0137 = 5 cents, then round) produces 5 cents.
        // This protects the critical "rounding happens ONCE" acceptance criterion.
        long accrued = calculator.accrueCents(1_00L, RATE,
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        assertEquals(5L, accrued,
                "full year at $1.00 and 5% must accrue exactly 5 cents (proves rounding is per-period, not per-day)");
    }
}
