package com.carereach.backend.repositories;

import com.carereach.backend.models.GramaNiladhariDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GramaNiladhariDivisionRepository extends JpaRepository<GramaNiladhariDivision, Long> {

    // Fetch all GN divisions belonging to a specific Divisional Secretariat ID
    List<GramaNiladhariDivision> findByDivisionalSecretariatId(Long dsId);
}
