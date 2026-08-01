package com.carereach.backend.services;

import com.carereach.backend.dtos.DoctorDashboardDto;
import com.carereach.backend.models.Patient;
import com.carereach.backend.models.User;
import com.carereach.backend.repositories.PatientRepository;
import com.carereach.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.carereach.backend.repositories.MedicalReportRepository medicalReportRepository;

    @Transactional(readOnly = true)
    public DoctorDashboardDto getDashboardStats(Long doctorId) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (doctor.getRole() != com.carereach.backend.models.Role.DOCTOR) {
            throw new RuntimeException("User is not a Doctor");
        }

        String ds = doctor.getDivisionalSecretariat();
        if (ds == null || ds.trim().isEmpty()) {
            // Fallback if doctor lacks a Divisional Secretariat
            ds = "Unknown";
        }

        List<Patient> verifiedPatientsRaw = patientRepository.findByDivisionalSecretariatAndStatusOrderByIdDesc(ds,
                "VERIFIED");

        // Filter out patients who already have at least one MedicalReport
        List<Patient> verifiedPatients = verifiedPatientsRaw.stream()
                .filter(p -> medicalReportRepository.findByPatientId(p.getId()).isEmpty())
                .collect(Collectors.toList());

        long pendingVisits = verifiedPatients.size();

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        long completedVisits = patientRepository.countCompletedVisits(ds, oneWeekAgo);

        long registeredPatients = patientRepository.countByDivisionalSecretariatAndHasAccount(ds, true);

        List<Map<String, Object>> verifiedList = verifiedPatients.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            map.put("district", p.getDistrict());
            map.put("gnDivision", p.getGnDivision());
            map.put("divisionalSecretariat", p.getDivisionalSecretariat());
            map.put("address", p.getAddress());
            map.put("gpsCoordinates", p.getGpsCoordinates());
            map.put("status", p.getStatus());

            if (p.getGnDivision() != null) {
                java.util.List<User> officers = userRepository
                        .findByRoleAndGnDivision(com.carereach.backend.models.Role.OFFICER, p.getGnDivision().trim());
                if (!officers.isEmpty()) {
                    map.put("verifiedGnName", officers.get(0).getFullName());
                } else {
                    map.put("verifiedGnName", "Grama Niladhari");
                }
            } else {
                map.put("verifiedGnName", "Grama Niladhari");
            }

            map.put("updatedAt", p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : "Recent");
            return map;
        }).collect(Collectors.toList());

        List<com.carereach.backend.models.MedicalReport> reports = medicalReportRepository.findByDoctorId(doctorId);
        java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Colombo"));

        List<Map<String, Object>> todayAdded = reports.stream()
                .filter(r -> r.getCreatedAt().toLocalDate().isEqual(today))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("patientId", r.getPatient().getId());
                    map.put("patientName", r.getPatient().getName());
                    map.put("diagnosis", r.getDiagnosis() != null ? r.getDiagnosis() : "No Summary Provided");

                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
                    map.put("completedOn", "Today, " + r.getCreatedAt().format(fmt));
                    return map;
                })
                .collect(Collectors.toList());

        DoctorDashboardDto dto = new DoctorDashboardDto();
        dto.setPendingVerifiedVisits(pendingVisits);
        dto.setCompletedVisits(completedVisits);
        dto.setRegisteredPatients(registeredPatients);
        dto.setVerifiedHomeVisits(verifiedList);
        dto.setTodayAdded(todayAdded);

        return dto;
    }

    @Transactional(readOnly = true)
    public com.carereach.backend.dtos.DoctorRegistryDto getRegistryData(Long doctorId) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }

        List<com.carereach.backend.models.MedicalReport> reports = medicalReportRepository.findByDoctorId(doctorId);

        // Group by Patient to get unique list and newest report per patient
        Map<Patient, com.carereach.backend.models.MedicalReport> latestReports = new HashMap<>();
        for (com.carereach.backend.models.MedicalReport r : reports) {
            Patient p = r.getPatient();
            if (!latestReports.containsKey(p) || r.getCreatedAt().isAfter(latestReports.get(p).getCreatedAt())) {
                latestReports.put(p, r);
            }
        }

        List<Map<String, Object>> activePatients = latestReports.entrySet().stream()
                .sorted((a, b) -> b.getValue().getCreatedAt().compareTo(a.getValue().getCreatedAt()))
                .map(entry -> {
                    Patient p = entry.getKey();
                    com.carereach.backend.models.MedicalReport latest = entry.getValue();

                    Map<String, Object> map = new HashMap<>();
                    map.put("patientId", p.getId());
                    map.put("name", p.getName());
                    map.put("symptoms", latest.getSymptoms());
                    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                            .ofPattern("MMM dd, yyyy hh:mm a");
                    map.put("lastVisit", latest.getCreatedAt() != null ? latest.getCreatedAt().format(fmt)
                            : (latest.getVisitDate() != null ? latest.getVisitDate() : "Unknown"));
                    map.put("nextVisit",
                            latest.getNextVisitDate() != null ? latest.getNextVisitDate() : "Not Scheduled");
                    return map;
                })
                .collect(Collectors.toList());

        com.carereach.backend.dtos.DoctorRegistryDto dto = new com.carereach.backend.dtos.DoctorRegistryDto();
        dto.setTotalPatients(latestReports.size());
        dto.setActivePatients(activePatients);

        return dto;
    }

    @Transactional(readOnly = true)
    public com.carereach.backend.dtos.DoctorAppointmentsDto getAppointments(Long doctorId) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }

        List<com.carereach.backend.models.MedicalReport> reports = medicalReportRepository.findByDoctorId(doctorId);
        java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Colombo"));

        // We interpret 'completedVisits' as any historical MedicalReport visit.
        List<Map<String, Object>> completedVisits = reports.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("patientName", r.getPatient().getName());
            map.put("patientId", r.getPatient().getId());
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                    .ofPattern("MMM dd, yyyy hh:mm a");
            map.put("appointmentDate", r.getCreatedAt() != null ? r.getCreatedAt().format(fmt)
                    : (r.getVisitDate() != null ? r.getVisitDate() : "Unknown"));
            map.put("status", "Completed");
            return map;
        })
                .sorted((a, b) -> b.get("appointmentDate").toString().compareTo(a.get("appointmentDate").toString()))
                .collect(Collectors.toList());

        // Find which patients have already been seen today
        List<Long> patientsSeenToday = reports.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().toLocalDate().isEqual(today))
                .map(r -> r.getPatient().getId())
                .collect(Collectors.toList());

        // We interpret 'upcomingVisits' as any target patient who has a MedicalReport
        // with a nextVisitDate exactly matching today.
        List<Map<String, Object>> upcomingVisits = reports.stream()
                .filter(r -> r.getNextVisitDate() != null
                        && java.time.LocalDate.parse(r.getNextVisitDate()).isEqual(today))
                .filter(r -> !patientsSeenToday.contains(r.getPatient().getId()))
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("patientName", r.getPatient().getName());
                    map.put("patientId", r.getPatient().getId());
                    map.put("gpsCoordinates", r.getPatient().getGpsCoordinates());
                    map.put("appointmentDate", r.getNextVisitDate());
                    map.put("lastVisitDate", r.getVisitDate() != null ? r.getVisitDate() : "Unknown");
                    map.put("status", "Pending");
                    return map;
                })
                // Resolve duplicate patients by picking the absolute closest upcoming
                // appointment
                .collect(Collectors.groupingBy(m -> m.get("patientId")))
                .values().stream()
                .map(list -> list.stream()
                        .min((a, b) -> a.get("appointmentDate").toString()
                                .compareTo(b.get("appointmentDate").toString()))
                        .get())
                .sorted((a, b) -> a.get("appointmentDate").toString().compareTo(b.get("appointmentDate").toString()))
                .collect(Collectors.toList());

        long completedTodayCount = reports.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().toLocalDate().isEqual(today))
                .count();

        long pendingTodayCount = reports.stream()
                .filter(r -> r.getNextVisitDate() != null
                        && java.time.LocalDate.parse(r.getNextVisitDate()).isEqual(today))
                .filter(r -> !patientsSeenToday.contains(r.getPatient().getId()))
                .map(r -> r.getPatient().getId())
                .distinct()
                .count();

        com.carereach.backend.dtos.DoctorAppointmentsDto dto = new com.carereach.backend.dtos.DoctorAppointmentsDto();
        dto.setUpcomingVisits(upcomingVisits);
        dto.setCompletedVisits(completedVisits);
        dto.setCompletedToday(completedTodayCount);
        dto.setPendingToday(pendingTodayCount);
        dto.setTotalToday(completedTodayCount + pendingTodayCount);

        return dto;
    }
}
