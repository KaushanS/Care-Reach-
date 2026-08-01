package com.carereach.backend.controllers;

import com.carereach.backend.dtos.PatientReportDto;
import com.carereach.backend.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/report")
    public ResponseEntity<?> reportPatient(@RequestBody PatientReportDto dto) {
        try {
            return ResponseEntity.ok(patientService.createPatientReport(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/report/{id}/verify")
    public ResponseEntity<?> verifyPatientReport(@PathVariable Long id) {
        try {
            patientService.verifyPatientReport(id);
            return ResponseEntity.ok("Successfully verified patient report");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/report/{id}/reject")
    public ResponseEntity<?> rejectPatientReport(@PathVariable Long id) {
        try {
            patientService.rejectPatientReport(id);
            return ResponseEntity.ok("Successfully rejected patient report");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPatients() {
        try {
            return ResponseEntity.ok(patientService.getAllPatients());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(patientService.getPatientById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPatientByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(patientService.getPatientByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/guardian/user/{userId}")
    public ResponseEntity<?> getPatientsByGuardianUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(patientService.getPatientsByGuardianUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updatePatientByUserId(@PathVariable Long userId,
            @RequestBody java.util.Map<String, Object> payload) {
        try {
            patientService.updatePatientProfile(userId, payload);
            return ResponseEntity.ok(java.util.Map.of("message", "Successfully updated patient profile"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(
                    "Validation Error: This email or phone number is already registered to another user account.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.ok("Successfully deleted patient profile");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
