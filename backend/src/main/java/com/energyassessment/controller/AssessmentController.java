package com.energyassessment.controller;

import com.energyassessment.dto.AssessmentRequest;
import com.energyassessment.dto.AssessmentResponse;
import com.energyassessment.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/assessment")
@Tag(name = "Assessment", description = "Submit and retrieve household energy assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Value("${app.admin.key}")
    private String adminKey;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    @Operation(summary = "Submit a new household energy assessment")
    public ResponseEntity<AssessmentResponse> submitAssessment(
            @Valid @RequestBody AssessmentRequest request) {

        AssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{assessmentId}")
    @Operation(summary = "Retrieve a previously submitted assessment by its anonymous ID")
    public ResponseEntity<AssessmentResponse> getAssessment(
            @PathVariable String assessmentId,
            @RequestHeader(value = "X-Admin-Key", required = false) String providedKey) {

        if (providedKey == null || !adminKey.equals(providedKey)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Admin authorization required"
            );
        }

        return ResponseEntity.ok(assessmentService.getAssessment(assessmentId));
    }
}