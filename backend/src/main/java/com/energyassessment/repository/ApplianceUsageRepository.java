package com.energyassessment.repository;

import com.energyassessment.entity.ApplianceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplianceUsageRepository extends JpaRepository<ApplianceUsage, Long> {
    List<ApplianceUsage> findByAssessment_AssessmentUuid(String assessmentUuid);
}
