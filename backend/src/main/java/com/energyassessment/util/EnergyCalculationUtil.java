package com.energyassessment.util;

/**
 * Pure, stateless energy math. Mirrors (and is the authoritative version of)
 * the calculations that used to live in the frontend's calculations.js.
 *
 * Kept dependency-free and static so it's trivial to unit test.
 */
public final class EnergyCalculationUtil {

    public static final String STATUS_BELOW = "BELOW_REFERENCE";
    public static final String STATUS_WITHIN = "WITHIN_REFERENCE_RANGE";
    public static final String STATUS_ABOVE = "ABOVE_REFERENCE";

    // A household/appliance within +/- this fraction of its reference is
    // considered "reasonably close" rather than meaningfully below/above.
    private static final double WITHIN_RANGE_TOLERANCE = 0.10;

    private EnergyCalculationUtil() {
    }

    public static double dailyEnergyKWh(double quantity, double powerWatts, double hoursPerDay) {
        return round((quantity * powerWatts * hoursPerDay) / 1000.0, 3);
    }

    public static double monthlyEnergyKWh(double dailyEnergyKWh) {
        return round(dailyEnergyKWh * 30.0, 2);
    }

    public static double monthlyCost(double monthlyEnergyKWh, double tariff) {
        return round(monthlyEnergyKWh * tariff, 2);
    }

    public static double referenceEnergyKWh(double quantity, double powerWatts, double referenceHoursPerDay) {
        return round((quantity * powerWatts * referenceHoursPerDay * 30.0) / 1000.0, 2);
    }

    public static double differenceKWh(double actualMonthlyKWh, double referenceMonthlyKWh) {
        return round(actualMonthlyKWh - referenceMonthlyKWh, 2);
    }

    /** Handles the zero-reference edge case safely, as required by the brief. */
    public static double percentageDifference(double actualMonthlyKWh, double referenceMonthlyKWh) {
        if (referenceMonthlyKWh == 0) return 0.0;
        return round(((actualMonthlyKWh - referenceMonthlyKWh) / referenceMonthlyKWh) * 100.0, 2);
    }

    /**
     * Three-band, non-judgmental classification:
     * BELOW_REFERENCE, WITHIN_REFERENCE_RANGE, ABOVE_REFERENCE.
     * Consumption depends on household size, appliances, climate and habits,
     * so this is a benchmark comparison, not a compliance verdict.
     */
    public static String classify(double actualMonthlyKWh, double referenceMonthlyKWh) {
        if (referenceMonthlyKWh <= 0) return STATUS_WITHIN;
        double lowerBound = referenceMonthlyKWh * (1 - WITHIN_RANGE_TOLERANCE);
        double upperBound = referenceMonthlyKWh * (1 + WITHIN_RANGE_TOLERANCE);
        if (actualMonthlyKWh < lowerBound) return STATUS_BELOW;
        if (actualMonthlyKWh > upperBound) return STATUS_ABOVE;
        return STATUS_WITHIN;
    }

    private static double round(double value, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(value * factor) / factor;
    }
}
