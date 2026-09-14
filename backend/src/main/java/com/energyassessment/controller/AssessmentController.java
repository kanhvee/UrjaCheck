package com.energyassessment.controller;

import com.energyassessment.dto.AssessmentRequest;
import com.energyassessment.dto.AssessmentResponse;
import com.energyassessment.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
@Tag(name = "Assessment", description = "Submit and retrieve household energy assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    @Operation(summary = "Submit a new household energy assessment")
    public ResponseEntity<AssessmentResponse> submitAssessment(@Valid @RequestBody AssessmentRequest request) {
        AssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{assessmentId}")
    @Operation(summary = "Retrieve a previously submitted assessment by its anonymous ID")
    public ResponseEntity<AssessmentResponse> getAssessment(@PathVariable String assessmentId) {
        return ResponseEntity.ok(assessmentService.getAssessment(assessmentId));
    }
}
