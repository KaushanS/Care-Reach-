package com.carereach.backend.repositories;

import com.carereach.backend.models.GramaNiladhariDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GramaNiladhariDivisionRepository extends JpaRepository<GramaNiladhariDivision, Long> {
    
    // Fetch all GN divisions 
    List<GramaNiladhariDivision> findByDivisionalSecretariatId(Long dsId);
    
    @Query("SELECT gn FROM GramaNiladhariDivision gn JOIN FETCH gn.divisionalSecretariat ds JOIN FETCH ds.district")
    List<GramaNiladhariDivision> findAll();
}
