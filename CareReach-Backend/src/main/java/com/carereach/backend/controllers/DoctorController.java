package com.carereach.backend.controllers;

import com.carereach.backend.dtos.DoctorDashboardDto;
import com.carereach.backend.services.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<?> getDashboardStats(@PathVariable Long id) {
        try {
            DoctorDashboardDto dto = doctorService.getDashboardStats(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/registry")
    public ResponseEntity<com.carereach.backend.dtos.DoctorRegistryDto> getRegistryData(@PathVariable Long id) {
        try {
            com.carereach.backend.dtos.DoctorRegistryDto data = doctorService.getRegistryData(id);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<com.carereach.backend.dtos.DoctorAppointmentsDto> getAppointments(@PathVariable Long id) {
        try {
            com.carereach.backend.dtos.DoctorAppointmentsDto data = doctorService.getAppointments(id);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
