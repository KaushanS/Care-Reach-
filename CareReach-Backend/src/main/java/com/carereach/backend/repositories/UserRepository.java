package com.carereach.backend.repositories;

import com.carereach.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByNic(String nic);

    boolean existsByUsernameAndIdNot(String username, Long id);

    long countByRole(com.carereach.backend.models.Role role);

    java.util.List<User> findByRole(com.carereach.backend.models.Role role);

    java.util.List<User> findByRoleAndGnDivision(com.carereach.backend.models.Role role, String gnDivision);
}
