package visible;

import com.worksim.corebank.service.InterestCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestVisibleTest {

    @Test
    void aFullYearOfInterestIsFivePercent() {
        long accrued = new InterestCalculator().accrueCents(10_000_00L, new BigDecimal("0.05"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        assertEquals(500_00L, accrued);
    }
}
