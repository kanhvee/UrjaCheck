package com.energyassessment.service;

import com.energyassessment.dto.ApplianceRequest;
import com.energyassessment.dto.AssessmentRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises the Bean Validation annotations on the request DTOs directly,
 * without needing a running Spring context — fast, and covers the
 * "invalid input" cases required by the brief (section 21 / 400 responses
 * in GlobalExceptionHandler build on exactly these constraints).
 */
class AssessmentValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private ApplianceRequest validAppliance() {
        ApplianceRequest a = new ApplianceRequest();
        a.setApplianceName("Ceiling Fan");
        a.setCategory("Cooling");
        a.setQuantity(4);
        a.setPowerWatts(75.0);
        a.setHoursPerDay(8.0);
        return a;
    }

    private AssessmentRequest validAssessment() {
        AssessmentRequest r = new AssessmentRequest();
        r.setHouseholdSize(4);
        r.setHouseType("2 BHK");
        r.setAppliances(List.of(validAppliance()));
        return r;
    }

    @Test
    void validAssessment_hasNoViolations() {
        Set<ConstraintViolation<AssessmentRequest>> violations = validator.validate(validAssessment());
        assertTrue(violations.isEmpty(), violations.toString());
    }

    @Test
    void householdSizeMustBeAtLeastOne() {
        AssessmentRequest r = validAssessment();
        r.setHouseholdSize(0);
        assertFalse(validator.validate(r).isEmpty());
    }

    @Test
    void assessmentRequiresAtLeastOneAppliance() {
        AssessmentRequest r = validAssessment();
        r.setAppliances(List.of());
        assertFalse(validator.validate(r).isEmpty());
    }

    @Test
    void quantityMustBePositive() {
        ApplianceRequest a = validAppliance();
        a.setQuantity(0);
        assertFalse(validator.validate(a).isEmpty());
    }

    @Test
    void powerMustBeGreaterThanZero() {
        ApplianceRequest a = validAppliance();
        a.setPowerWatts(0.0);
        assertFalse(validator.validate(a).isEmpty());
    }

    @Test
    void hoursPerDayMustBeWithinZeroToTwentyFour() {
        ApplianceRequest tooHigh = validAppliance();
        tooHigh.setHoursPerDay(25.0);
        assertFalse(validator.validate(tooHigh).isEmpty());

        ApplianceRequest negative = validAppliance();
        negative.setHoursPerDay(-1.0);
        assertFalse(validator.validate(negative).isEmpty());

        ApplianceRequest zero = validAppliance();
        zero.setHoursPerDay(0.0);
        assertTrue(validator.validate(zero).isEmpty());
    }
}
