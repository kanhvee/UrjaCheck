package com.energyassessment.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyCalculationUtilTest {

    // The canonical example from the project brief:
    // 4 ceiling fans x 75W x 8 hours/day
    @Test
    void dailyEnergy_matchesBriefExample() {
        double daily = EnergyCalculationUtil.dailyEnergyKWh(4, 75, 8);
        assertEquals(2.4, daily, 0.0001);
    }

    @Test
    void monthlyEnergy_matchesBriefExample() {
        double monthly = EnergyCalculationUtil.monthlyEnergyKWh(2.4);
        assertEquals(72.0, monthly, 0.0001);
    }

    @Test
    void monthlyCost_matchesBriefExample() {
        double cost = EnergyCalculationUtil.monthlyCost(72.0, 8.0);
        assertEquals(576.0, cost, 0.0001);
    }

    @Test
    void referenceEnergy_matchesBriefExample() {
        // 4 x 75W x 7 reference hours/day x 30 / 1000 = 63 kWh/month
        double reference = EnergyCalculationUtil.referenceEnergyKWh(4, 75, 7);
        assertEquals(63.0, reference, 0.0001);
    }

    @Test
    void percentageDifference_matchesBriefExample() {
        double pct = EnergyCalculationUtil.percentageDifference(72.0, 63.0);
        assertEquals(14.29, pct, 0.01);
    }

    @Test
    void percentageDifference_handlesZeroReferenceSafely() {
        double pct = EnergyCalculationUtil.percentageDifference(50.0, 0.0);
        assertEquals(0.0, pct, 0.0001);
    }

    @Test
    void classify_withinTenPercentBandIsWithinReference() {
        assertEquals(EnergyCalculationUtil.STATUS_WITHIN, EnergyCalculationUtil.classify(100, 95));
        assertEquals(EnergyCalculationUtil.STATUS_WITHIN, EnergyCalculationUtil.classify(100, 100));
        assertEquals(EnergyCalculationUtil.STATUS_WITHIN, EnergyCalculationUtil.classify(100, 109));
    }

    @Test
    void classify_aboveTenPercentIsAboveReference() {
        // brief's own example: 72 vs 63 is ~14.29% above -> ABOVE_REFERENCE
        assertEquals(EnergyCalculationUtil.STATUS_ABOVE, EnergyCalculationUtil.classify(72, 63));
    }

    @Test
    void classify_belowTenPercentIsBelowReference() {
        assertEquals(EnergyCalculationUtil.STATUS_BELOW, EnergyCalculationUtil.classify(50, 100));
    }
}
