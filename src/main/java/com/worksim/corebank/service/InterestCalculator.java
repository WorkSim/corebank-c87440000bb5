package com.worksim.corebank.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InterestCalculator {

    /**
     * Actual/360 accrual: principal x rate x (actual days in the period) / 360.
     * The 360-day banking-year divisor is the standard money-market
     * convention, so every month contributes a consistent daily rate
     * regardless of how many days the calendar gives it.
     *
     * Rounding happens ONCE, on the period total, so daily rounding error
     * cannot accumulate and the accrued figure reconciles to the statement.
     */
    public long accrueCents(long principalCents, BigDecimal annualRate, LocalDate from, LocalDate to) {
        BigDecimal principal = BigDecimal.valueOf(principalCents);
        long days = ChronoUnit.DAYS.between(from, to);
        BigDecimal total = principal.multiply(annualRate)
                .divide(BigDecimal.valueOf(360), MathContext.DECIMAL128)
                .multiply(BigDecimal.valueOf(days));
        return total.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
