package com.energyassessment.controller;

import com.energyassessment.dto.ReferenceStandardResponse;
import com.energyassessment.service.ReferenceStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reference-standards")
@Tag(name = "Reference Standards", description = "Active benchmark/reference rows, with source transparency metadata")
public class ReferenceStandardController {

    private final ReferenceStandardService referenceStandardService;

    public ReferenceStandardController(ReferenceStandardService referenceStandardService) {
        this.referenceStandardService = referenceStandardService;
    }

    @GetMapping
    @Operation(summary = "List all active reference standards used for comparison")
    public List<ReferenceStandardResponse> getActiveStandards() {
        return referenceStandardService.getActiveStandards();
    }
}
