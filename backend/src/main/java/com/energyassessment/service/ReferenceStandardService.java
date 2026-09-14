package com.energyassessment.service;

import com.energyassessment.dto.ReferenceSourceDto;
import com.energyassessment.dto.ReferenceStandardResponse;
import com.energyassessment.entity.ReferenceStandard;
import com.energyassessment.repository.ReferenceStandardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceStandardService {

    private final ReferenceStandardRepository referenceStandardRepository;

    // Used only when an appliance has no seeded reference row at all —
    // clearly marked as a project assumption, never as an official figure.
    private static final double FALLBACK_REFERENCE_HOURS = 2.0;

    public ReferenceStandardService(ReferenceStandardRepository referenceStandardRepository) {
        this.referenceStandardRepository = referenceStandardRepository;
    }

    /** Reference hours/day to use in the calculation for this appliance. */
    public double getReferenceHours(String applianceName) {
        return referenceStandardRepository
                .findFirstByApplianceNameIgnoreCaseAndActiveTrueAndReferenceHoursPerDayIsNotNull(applianceName)
                .map(ReferenceStandard::getReferenceHoursPerDay)
                .orElse(FALLBACK_REFERENCE_HOURS);
    }

    /** Transparency metadata to attach to the appliance result. */
    public ReferenceSourceDto getReferenceSource(String applianceName) {
        return referenceStandardRepository
                .findFirstByApplianceNameIgnoreCaseAndActiveTrueAndReferenceHoursPerDayIsNotNull(applianceName)
                .map(rs -> new ReferenceSourceDto(
                        rs.getReferenceType(),
                        rs.getSourceOrganization(),
                        rs.getSourceDocument(),
                        rs.getSourceUrl(),
                        rs.getApplicableYear(),
                        rs.getNotes()))
                .orElseGet(() -> new ReferenceSourceDto(
                        "PROJECT_REFERENCE",
                        "UrjaCheck Project",
                        "No specific reference defined for this appliance type",
                        null,
                        null,
                        "This appliance is not in the seeded reference table; a generic " +
                                FALLBACK_REFERENCE_HOURS + " hours/day project assumption was used."
                ));
    }

    public List<ReferenceStandardResponse> getActiveStandards() {
        return referenceStandardRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    private ReferenceStandardResponse toResponse(ReferenceStandard rs) {
        ReferenceStandardResponse r = new ReferenceStandardResponse();
        r.setId(rs.getId());
        r.setApplianceCategory(rs.getApplianceCategory());
        r.setApplianceName(rs.getApplianceName());
        r.setReferenceType(rs.getReferenceType());
        r.setReferenceValue(rs.getReferenceValue());
        r.setUnit(rs.getUnit());
        r.setReferenceHoursPerDay(rs.getReferenceHoursPerDay());
        r.setSourceOrganization(rs.getSourceOrganization());
        r.setSourceDocument(rs.getSourceDocument());
        r.setSourceUrl(rs.getSourceUrl());
        r.setApplicableYear(rs.getApplicableYear());
        r.setNotes(rs.getNotes());
        return r;
    }
}
