package com.carereach.backend.repositories;

import com.carereach.backend.models.DoctorCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorCareerRepository extends JpaRepository<DoctorCareer, Long> {
    Optional<DoctorCareer> findByUserId(Long userId);
}
