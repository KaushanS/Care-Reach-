package com.carereach.backend.repositories;

import com.carereach.backend.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop5ByTargetUser_IdOrderByCreatedAtDesc(Long userId);

    List<Notification> findAllByTargetUser_IdOrderByCreatedAtDesc(Long userId);

    long countByTargetUser_IdAndIsReadFalse(Long userId);

    void deleteAllByTargetUser_Id(Long userId);
}
