package com.energyassessment.repository;

import com.energyassessment.entity.ReferenceStandard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferenceStandardRepository extends JpaRepository<ReferenceStandard, Long> {
    List<ReferenceStandard> findByActiveTrue();

    // Picks a specific calculation reference (skips general STAR_LABEL_INFO rows).
    Optional<ReferenceStandard> findFirstByApplianceNameIgnoreCaseAndActiveTrueAndReferenceHoursPerDayIsNotNull(String applianceName);
}
