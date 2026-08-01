package com.carereach.backend.repositories;

import com.carereach.backend.models.DivisionalSecretariat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DivisionalSecretariatRepository extends JpaRepository<DivisionalSecretariat, Long> {

    List<DivisionalSecretariat> findByDistrictId(Long districtId);
}
