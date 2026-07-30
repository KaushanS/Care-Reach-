package com.carereach.backend.repositories;

import com.carereach.backend.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByNic(String nic);

    Optional<Patient> findByNic(String nic);

    boolean existsByPatientEmail(String patientEmail);

    long countByReportedById(Long id);

    long countByGuardianEmail(String guardianEmail);

    List<Patient> findByGuardianEmail(String guardianEmail);

    long countByReportedByIdAndStatus(Long id, String status);

    List<Patient> findTop5ByReportedByIdOrderByIdDesc(Long id);

    List<Patient> findAllByReportedByIdOrderByIdDesc(Long id);

    long countByStatusNot(String status);

    long countByStatus(String status);

    List<Patient> findTop5ByOrderByIdDesc();

    long countByDivisionalSecretariatAndStatus(String ds, String status);

    long countByDivisionalSecretariatAndHasAccount(String ds, boolean hasAccount);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(p) FROM Patient p WHERE p.divisionalSecretariat = :ds AND p.hasAccount = true AND p.updatedAt >= :weekAgo")
    long countCompletedVisits(@org.springframework.data.repository.query.Param("ds") String ds,
            @org.springframework.data.repository.query.Param("weekAgo") java.time.LocalDateTime weekAgo);

    List<Patient> findByDivisionalSecretariatAndStatusOrderByIdDesc(String ds, String status);

    java.util.Optional<Patient> findByPatientEmail(String patientEmail);

    @org.springframework.data.jpa.repository.Query(value = "SELECT district AS name, COUNT(*) AS count FROM patients WHERE district IS NOT NULL GROUP BY district ORDER BY count DESC LIMIT 5", nativeQuery = true)
    List<Object[]> countTopDistricts();

    @org.springframework.data.jpa.repository.Query(value = "SELECT MONTHNAME(created_at) AS month, COUNT(*) AS count FROM patients GROUP BY MONTHNAME(created_at), MONTH(created_at) ORDER BY MONTH(created_at)", nativeQuery = true)
    List<Object[]> countPatientsByMonth();
}
