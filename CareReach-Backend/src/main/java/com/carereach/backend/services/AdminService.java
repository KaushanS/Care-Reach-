package com.carereach.backend.services;

import com.carereach.backend.dtos.AdminDashboardDto;
import com.carereach.backend.dtos.AdminAnalyticsDto;
import com.carereach.backend.dtos.OfficerRegistrationDto;
import java.util.ArrayList;
import java.util.Collections;
import com.carereach.backend.dtos.PatientReportSummaryDto;
import com.carereach.backend.models.Patient;
import com.carereach.backend.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.carereach.backend.models.Role;
import com.carereach.backend.models.User;
import com.carereach.backend.dtos.AdminDoctorDto;
import com.carereach.backend.repositories.UserRepository;
import com.carereach.backend.repositories.DoctorCareerRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorCareerRepository doctorCareerRepository;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public AdminAnalyticsDto getAnalyticsData() {
        AdminAnalyticsDto dto = new AdminAnalyticsDto();

        dto.setVerifiedCount(patientRepository.countByStatus("VERIFIED"));
        dto.setPendingCount(patientRepository.countByStatus("PENDING_VERIFY"));
        dto.setRejectedCount(patientRepository.countByStatus("REJECTED"));

        List<Object[]> districtData = patientRepository.countTopDistricts();
        List<String> dLabels = new ArrayList<>();
        List<Long> dCounts = new ArrayList<>();
        for (Object[] row : districtData) {
            dLabels.add((String) row[0]);
            dCounts.add(((Number) row[1]).longValue());
        }
        dto.setDistrictLabels(dLabels);
        dto.setDistrictCounts(dCounts);

        List<Object[]> monthData = patientRepository.countPatientsByMonth();
        List<String> mLabels = new ArrayList<>();
        List<Long> mCounts = new ArrayList<>();
        for (Object[] row : monthData) {
            mLabels.add((String) row[0]);
            mCounts.add(((Number) row[1]).longValue());
        }
        dto.setMonthlyLabels(mLabels);
        dto.setMonthlyCounts(mCounts);

        dto.setDoctorCount(userRepository.countByRole(Role.DOCTOR));
        dto.setOfficerCount(userRepository.countByRole(Role.OFFICER));
        dto.setGuardianCount(userRepository.countByRole(Role.GUARDIAN));
        dto.setVolunteerCount(userRepository.countByRole(Role.VOLUNTEER));

        return dto;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalPatients(patientRepository.count());
        dto.setVerifiedPatients(patientRepository.countByStatus("VERIFIED"));
        dto.setPendingPatients(patientRepository.countByStatus("PENDING_VERIFY"));
        dto.setRejectedPatients(patientRepository.countByStatus("REJECTED"));

        // Let's fetch the top 5 newest emergencies across the platform
        // Wait, I need PatientRepository.findTop5ByOrderByIdDesc()
        List<Patient> recent = patientRepository.findTop5ByOrderByIdDesc();

        List<PatientReportSummaryDto> recentDtos = recent.stream().map(p -> {
            PatientReportSummaryDto pt = new PatientReportSummaryDto();
            pt.setId(p.getId());
            pt.setName(p.getName());
            pt.setNic(p.getNic());
            pt.setDistrict(p.getDistrict());
            pt.setDivisionalSecretariat(p.getDivisionalSecretariat());
            pt.setStatus(p.getStatus());

            if (p.getReportedBy() != null) {
                pt.setReportedBy(p.getReportedBy().getFullName());
                pt.setReporterPhoto(p.getReportedBy().getProfilePhoto());
            } else {
                pt.setReportedBy("System");
            }
            pt.setCreatedAt(p.getCreatedAt());
            return pt;
        }).collect(Collectors.toList());

        dto.setRecentEmergencies(recentDtos);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<User> getAllOfficers() {
        return userRepository.findByRole(Role.OFFICER);
    }

    @Transactional
    public User registerGNOfficer(OfficerRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        if (dto.getNic() != null && userRepository.findByNic(dto.getNic()).isPresent()) {
            throw new RuntimeException("NIC is already registered!");
        }

        User newUser = new User();
        newUser.setFullName(dto.getFullName());
        // Auto-generate username from GN Division Name
        String cleanGn = dto.getGnDivisionName().toLowerCase().replaceAll("[^a-z0-9]", "");
        newUser.setUsername("gn_" + cleanGn);

        newUser.setEmail(dto.getEmail());
        newUser.setNic(dto.getNic());
        newUser.setPhone(dto.getPhone());

        // Combining GN Name + Number for the GN Division string matching frontend
        // filters
        String gnDiv = dto.getGnDivisionName();
        if (dto.getGnDivisionNumber() != null && !dto.getGnDivisionNumber().isEmpty()) {
            gnDiv += " (GN-" + dto.getGnDivisionNumber() + ")";
        }
        newUser.setGnDivision(gnDiv);
        newUser.setAddress(dto.getAddress());
        newUser.setDistrict(dto.getDistrict());
        newUser.setDivisionalSecretariat(dto.getDivisionalSecretariat());
        newUser.setRole(Role.OFFICER);

        String tempPassword = "Welcome@123";
        newUser.setPassword(tempPassword);
        newUser.setRequiresCredentialReset(true);

        User savedUser = userRepository.save(newUser);

        emailService.sendProvisioningEmail(savedUser.getEmail(), savedUser.getFullName(), savedUser.getUsername(),
                tempPassword, "Grama Niladhari");

        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<AdminDoctorDto> getAllDoctors() {
        List<User> doctors = userRepository.findByRole(Role.DOCTOR);
        return doctors.stream().map(doc -> {
            AdminDoctorDto dto = new AdminDoctorDto();
            dto.setId(doc.getId());
            dto.setFullName(doc.getFullName());
            dto.setEmail(doc.getEmail());
            dto.setPhone(doc.getPhone());
            dto.setNic(doc.getNic());
            dto.setUsername(doc.getUsername());
            dto.setAddress(doc.getAddress());
            dto.setCreatedAt(doc.getCreatedAt());
            dto.setDistrict(doc.getDistrict());
            dto.setDivisionalSecretariat(doc.getDivisionalSecretariat());
            dto.setProfilePhoto(doc.getProfilePhoto());

            doctorCareerRepository.findByUserId(doc.getId()).ifPresent(career -> {
                dto.setSpecialization(career.getSpecialization());
                dto.setHospitalName(career.getHospitalName());
                dto.setQualification(career.getQualification());
                dto.setExperienceYears(career.getExperienceYears());
            });

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.carereach.backend.dtos.AdminGuardianDto> getAllGuardians() {
        List<User> guardians = userRepository.findByRole(Role.GUARDIAN);
        return guardians.stream().map(g -> {
            com.carereach.backend.dtos.AdminGuardianDto dto = new com.carereach.backend.dtos.AdminGuardianDto();
            dto.setId(g.getId());
            dto.setFullName(g.getFullName());
            dto.setEmail(g.getEmail());
            dto.setPhone(g.getPhone());
            dto.setNic(g.getNic());
            dto.setAddress(g.getAddress());
            dto.setDivisionalSecretariat(g.getDivisionalSecretariat());
            dto.setDistrict(g.getDistrict());
            dto.setProfilePhoto(g.getProfilePhoto());
            dto.setLinkedPatientsCount(patientRepository.countByGuardianEmail(g.getEmail()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.carereach.backend.dtos.AdminVolunteerDto> getAllVolunteers() {
        List<User> volunteers = userRepository.findByRole(Role.VOLUNTEER);
        return volunteers.stream().map(v -> {
            com.carereach.backend.dtos.AdminVolunteerDto dto = new com.carereach.backend.dtos.AdminVolunteerDto();
            dto.setId(v.getId());
            dto.setFullName(v.getFullName());
            dto.setEmail(v.getEmail());
            dto.setPhone(v.getPhone());
            dto.setNic(v.getNic());
            dto.setAddress(v.getAddress());
            dto.setDivisionalSecretariat(v.getDivisionalSecretariat());
            dto.setDistrict(v.getDistrict());
            dto.setGnDivision(v.getGnDivision());
            dto.setProfilePhoto(v.getProfilePhoto());
            dto.setReportedPatientsCount(patientRepository.countByReportedById(v.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
