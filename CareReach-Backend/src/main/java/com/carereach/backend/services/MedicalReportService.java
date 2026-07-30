package com.carereach.backend.services;

import com.carereach.backend.dtos.MedicalReportDto;
import com.carereach.backend.models.MedicalReport;
import com.carereach.backend.models.Patient;
import com.carereach.backend.models.User;
import com.carereach.backend.repositories.MedicalReportRepository;
import com.carereach.backend.repositories.PatientRepository;
import com.carereach.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalReportService {

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MedicalReportDto createMedicalReport(MedicalReportDto dto) {
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("Patient ID is required.");
        }
        if (dto.getDoctorId() == null) {
            throw new IllegalArgumentException("Doctor ID is required.");
        }

        Patient patient = patientRepository.findById(dto.getPatientId().longValue())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + dto.getPatientId()));

        User doctor = userRepository.findById(dto.getDoctorId().longValue())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));

        MedicalReport report = new MedicalReport();
        report.setPatient(patient);
        report.setDoctor(doctor);
        report.setVisitDate(dto.getVisitDate());
        report.setNextVisitDate(dto.getNextVisitDate());
        report.setSymptoms(dto.getSymptoms());
        report.setDiagnosis(dto.getDiagnosis());
        report.setMedicines(dto.getMedicines());
        report.setDescription(dto.getDescription());

        MedicalReport saved = medicalReportRepository.save(report);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicalReportDto> getReportsByPatientId(Long patientId) {
        return medicalReportRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MedicalReportDto mapToDto(MedicalReport report) {
        MedicalReportDto dto = new MedicalReportDto();
        dto.setId(report.getId());
        dto.setPatientId(report.getPatient().getId());
        dto.setDoctorId(report.getDoctor().getId());
        dto.setVisitDate(report.getVisitDate());
        dto.setNextVisitDate(report.getNextVisitDate());
        dto.setSymptoms(report.getSymptoms());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setMedicines(report.getMedicines());
        dto.setDescription(report.getDescription());
        dto.setDoctorName(report.getDoctor().getFullName());
        return dto;
    }
}
