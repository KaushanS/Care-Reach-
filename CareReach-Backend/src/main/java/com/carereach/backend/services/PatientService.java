package com.carereach.backend.services;

import com.carereach.backend.dtos.PatientReportDto;
import com.carereach.backend.models.Patient;
import com.carereach.backend.models.User;
import com.carereach.backend.repositories.PatientRepository;
import com.carereach.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.carereach.backend.repositories.NotificationRepository notificationRepository;

    public Patient createPatientReport(PatientReportDto dto) {
        Long reportedById = dto.getReportedById();
        if (reportedById == null) {
            throw new IllegalArgumentException("Reported by ID cannot be null");
        }

        User volunteer = userRepository.findById(reportedById)
                .orElseThrow(() -> new RuntimeException("Volunteer User not found"));

        if (dto.getNic() != null && !dto.getNic().trim().isEmpty()) {
            if (patientRepository.existsByNic(dto.getNic())) {
                throw new RuntimeException("A patient with this NIC is already registered in the system.");
            }
        }

        if (dto.getPatientEmail() != null && !dto.getPatientEmail().trim().isEmpty()) {
            if (patientRepository.existsByPatientEmail(dto.getPatientEmail())) {
                throw new RuntimeException("A patient with this Email is already registered in the system.");
            }
        }

        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setNic(dto.getNic());
        patient.setPatientEmail(
                dto.getPatientEmail() != null && dto.getPatientEmail().trim().isEmpty() ? null : dto.getPatientEmail());
        patient.setPatientPhone(dto.getPatientPhone());
        patient.setGuardianName(dto.getGuardianName());
        patient.setGuardianPhone(dto.getGuardianPhone());
        patient.setGuardianEmail(dto.getGuardianEmail());
        patient.setAddress(dto.getAddress());
        patient.setDistrict(dto.getDistrict());
        patient.setDivisionalSecretariat(dto.getDivisionalSecretariat());
        patient.setGnDivision(dto.getGnDivision());
        patient.setGpsCoordinates(dto.getGpsCoordinates());
        patient.setDescription(dto.getDescription());

        patient.setStatus("PENDING_VERIFY");
        patient.setReportedBy(volunteer);

        Patient savedPatient = patientRepository.save(patient);

        com.carereach.backend.models.Notification n = new com.carereach.backend.models.Notification();
        n.setTargetUser(volunteer);
        n.setMessage("Report successfully submitted for subject: " + patient.getName());
        n.setType("SUCCESS");
        n.setRead(false);
        notificationRepository.save(n);

        if (patient.getGnDivision() != null) {
            List<User> officers = userRepository.findByRoleAndGnDivision(com.carereach.backend.models.Role.OFFICER,
                    patient.getGnDivision());
            for (User off : officers) {
                com.carereach.backend.models.Notification on = new com.carereach.backend.models.Notification();
                on.setTargetUser(off);
                on.setMessage(volunteer.getFullName() + " submitted a new patient report: " + patient.getName());
                on.setType("ALERT");
                on.setRead(false);
                notificationRepository.save(on);
            }
        }

        return savedPatient;
    }

    public Patient verifyPatientReport(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }
        Patient patient = patientRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Patient Report not found"));

        patient.setStatus("VERIFIED");
        Patient savedPatient = patientRepository.save(patient);

        if (patient.getReportedBy() != null) {
            com.carereach.backend.models.Notification n = new com.carereach.backend.models.Notification();
            n.setTargetUser(patient.getReportedBy());
            n.setMessage("Your report for " + patient.getName() + " has been VERIFIED.");
            n.setType("SUCCESS");
            n.setRead(false);
            notificationRepository.save(n);
        }

        if (patient.getGnDivision() != null) {
            List<User> officers = userRepository.findByRoleAndGnDivision(com.carereach.backend.models.Role.OFFICER,
                    patient.getGnDivision());
            for (User off : officers) {
                com.carereach.backend.models.Notification on = new com.carereach.backend.models.Notification();
                on.setTargetUser(off);
                on.setMessage("You successfully verified the report for " + patient.getName());
                on.setType("SUCCESS");
                on.setRead(false);
                notificationRepository.save(on);
            }
        }

        return savedPatient;
    }

    public Patient rejectPatientReport(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }
        Patient patient = patientRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Patient Report not found"));

        patient.setStatus("REJECTED");
        Patient savedPatient = patientRepository.save(patient);

        if (patient.getReportedBy() != null) {
            com.carereach.backend.models.Notification n = new com.carereach.backend.models.Notification();
            n.setTargetUser(patient.getReportedBy());
            n.setMessage("Your report for " + patient.getName() + " was REJECTED.");
            n.setType("ALERT");
            n.setRead(false);
            notificationRepository.save(n);
        }

        if (patient.getGnDivision() != null) {
            List<User> officers = userRepository.findByRoleAndGnDivision(com.carereach.backend.models.Role.OFFICER,
                    patient.getGnDivision());
            for (User off : officers) {
                com.carereach.backend.models.Notification on = new com.carereach.backend.models.Notification();
                on.setTargetUser(off);
                on.setMessage("You have rejected the report for " + patient.getName());
                on.setType("INFO");
                on.setRead(false);
                notificationRepository.save(on);
            }
        }

        return savedPatient;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllPatients() {
        return patientRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("address", p.getAddress());
            map.put("hasAccount", p.isHasAccount());
            map.put("hasGuardianAccount", p.isHasGuardianAccount());
            map.put("district", p.getDistrict());
            map.put("status", p.getStatus());
            map.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
            map.put("updatedAt", p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null);
            map.put("age", p.getAge());
            map.put("gender", p.getGender());
            map.put("nic", p.getNic());
            map.put("patientEmail", p.getPatientEmail());
            map.put("patientPhone", p.getPatientPhone());
            map.put("guardianName", p.getGuardianName());
            map.put("guardianPhone", p.getGuardianPhone());
            map.put("guardianEmail", p.getGuardianEmail());
            if (p.getGnDivision() != null) {
                map.put("gnDivision", p.getGnDivision());
            } else {
                map.put("gnDivision", null);
            }
            if ("VERIFIED".equals(p.getStatus()) && p.getGnDivision() != null) {
                java.util.List<User> officers = userRepository
                        .findByRoleAndGnDivision(com.carereach.backend.models.Role.OFFICER, p.getGnDivision().trim());
                if (!officers.isEmpty()) {
                    map.put("verifiedGnName", officers.get(0).getFullName());
                } else {
                    map.put("verifiedGnName", "System Admin");
                }
            } else {
                map.put("verifiedGnName", "Pending");
            }
            map.put("divisionalSecretariat", p.getDivisionalSecretariat());
            map.put("gpsCoordinates", p.getGpsCoordinates());
            map.put("description", p.getDescription());

            if (p.isHasAccount()) {
                User ptUser = null;
                if (p.getNic() != null && !p.getNic().isEmpty()) {
                    ptUser = userRepository.findByNic(p.getNic()).orElse(null);
                }
                if (ptUser == null && p.getPatientEmail() != null && !p.getPatientEmail().isEmpty()) {
                    ptUser = userRepository.findByEmail(p.getPatientEmail()).orElse(null);
                }
                if (ptUser != null) {
                    map.put("profilePhoto", ptUser.getProfilePhoto());
                }
            }

            Map<String, Object> reporter = new HashMap<>();
            if (p.getReportedBy() != null) {
                reporter.put("fullName", p.getReportedBy().getFullName());
                reporter.put("profilePhoto", p.getReportedBy().getProfilePhoto());
            } else {
                reporter.put("fullName", "System Source");
            }
            map.put("reportedBy", reporter);
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPatientById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("address", p.getAddress());
        map.put("hasAccount", p.isHasAccount());
        map.put("hasGuardianAccount", p.isHasGuardianAccount());
        map.put("district", p.getDistrict());
        map.put("status", p.getStatus());
        map.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
        map.put("updatedAt", p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null);
        map.put("age", p.getAge());
        map.put("gender", p.getGender());
        map.put("nic", p.getNic());
        map.put("patientEmail", p.getPatientEmail());
        map.put("patientPhone", p.getPatientPhone());
        map.put("guardianName", p.getGuardianName());
        map.put("guardianPhone", p.getGuardianPhone());
        map.put("guardianEmail", p.getGuardianEmail());
        map.put("gnDivision", p.getGnDivision());
        map.put("divisionalSecretariat", p.getDivisionalSecretariat());
        map.put("gpsCoordinates", p.getGpsCoordinates());
        map.put("description", p.getDescription());

        Map<String, Object> reporter = new HashMap<>();
        if (p.getReportedBy() != null) {
            reporter.put("fullName", p.getReportedBy().getFullName());
            reporter.put("phone", p.getReportedBy().getPhone());
            reporter.put("profilePhoto", p.getReportedBy().getProfilePhoto());
        } else {
            reporter.put("fullName", "System Source");
        }
        map.put("reportedBy", reporter);
        return map;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPatientByUserId(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Patient p = null;
        if (user.getNic() != null && !user.getNic().isEmpty()) {
            p = patientRepository.findByNic(user.getNic()).orElse(null);
        }
        if (p == null && user.getEmail() != null) {
            p = patientRepository.findByPatientEmail(user.getEmail()).orElse(null);
        }
        if (p == null) {
            throw new RuntimeException("No matched Patient profile found for this user account.");
        }
        Map<String, Object> map = getPatientById(p.getId());
        map.put("profilePhoto", user.getProfilePhoto());
        return map;
    }

    @Transactional
    public void updatePatientProfile(Long userId, Map<String, Object> payload) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Patient p = null;
        if (user.getNic() != null && !user.getNic().isEmpty()) {
            p = patientRepository.findByNic(user.getNic()).orElse(null);
        }
        if (p == null && user.getEmail() != null) {
            p = patientRepository.findByPatientEmail(user.getEmail()).orElse(null);
        }
        if (p == null) {
            throw new RuntimeException("No matched Patient profile found for this user account.");
        }

        if (payload.containsKey("name") && payload.get("name") != null
                && !payload.get("name").toString().trim().isEmpty()) {
            String name = (String) payload.get("name");
            p.setName(name);
            user.setFullName(name);
        }
        if (payload.containsKey("patientEmail") && payload.get("patientEmail") != null
                && !payload.get("patientEmail").toString().trim().isEmpty()) {
            String email = (String) payload.get("patientEmail");
            p.setPatientEmail(email);
            user.setEmail(email);
        }
        if (payload.containsKey("patientPhone") && payload.get("patientPhone") != null) {
            String phone = (String) payload.get("patientPhone");
            p.setPatientPhone(phone);
            user.setPhone(phone);
        }
        if (payload.containsKey("address") && payload.get("address") != null) {
            p.setAddress((String) payload.get("address"));
        }
        if (payload.containsKey("age") && payload.get("age") != null
                && !payload.get("age").toString().trim().isEmpty()) {
            try {
                p.setAge(Integer.valueOf(payload.get("age").toString().trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        if (payload.containsKey("gender") && payload.get("gender") != null) {
            p.setGender((String) payload.get("gender"));
        }
        if (payload.containsKey("guardianName") && payload.get("guardianName") != null) {
            p.setGuardianName((String) payload.get("guardianName"));
        }
        if (payload.containsKey("guardianPhone") && payload.get("guardianPhone") != null) {
            p.setGuardianPhone((String) payload.get("guardianPhone"));
        }
        if (payload.containsKey("guardianEmail") && payload.get("guardianEmail") != null) {
            p.setGuardianEmail((String) payload.get("guardianEmail"));
        }
        patientRepository.save(p);
        userRepository.save(user);
    }

    @Transactional
    public void deletePatient(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found!");
        }
        patientRepository.deleteById(id);
    }

    public List<Map<String, Object>> getPatientsByGuardianUserId(Long guardianUserId) {
        if (guardianUserId == null)
            throw new IllegalArgumentException("Guardian User ID cannot be null");
        User guardianUser = userRepository.findById(guardianUserId)
                .orElseThrow(() -> new RuntimeException("Guardian User not found"));

        List<Patient> patients = patientRepository.findByGuardianEmail(guardianUser.getEmail());

        return patients.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("age", p.getAge());
            map.put("gender", p.getGender());
            map.put("nic", p.getNic());
            map.put("patientEmail", p.getPatientEmail());
            map.put("patientPhone", p.getPatientPhone());
            map.put("address", p.getAddress());
            map.put("description", p.getDescription());
            map.put("status", p.getStatus());
            map.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
            return map;
        }).collect(Collectors.toList());
    }
}
