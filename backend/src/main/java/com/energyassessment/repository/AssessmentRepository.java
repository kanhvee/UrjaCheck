package com.energyassessment.repository;

import com.energyassessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    Optional<Assessment> findByAssessmentUuid(String assessmentUuid);
    long countByStatus(String status);
}
