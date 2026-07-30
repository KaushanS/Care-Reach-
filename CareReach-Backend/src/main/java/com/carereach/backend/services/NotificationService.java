package com.carereach.backend.services;

import com.carereach.backend.dtos.NotificationDto;
import com.carereach.backend.models.Notification;
import com.carereach.backend.models.User;
import com.carereach.backend.repositories.NotificationRepository;
import com.carereach.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationDto> getLatestNotifications(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return notificationRepository.findTop5ByTargetUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getAllNotifications(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return notificationRepository.findAllByTargetUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return notificationRepository.countByTargetUser_IdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void createNotification(Long targetUserId, String message, String type) {
        if (targetUserId == null) {
            throw new IllegalArgumentException("Target User ID cannot be null");
        }
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification n = new Notification();
        n.setTargetUser(user);
        n.setMessage(message);
        n.setType(type);
        notificationRepository.save(n);
    }

    private NotificationDto mapToDto(Notification notif) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notif.getId());
        dto.setMessage(notif.getMessage());
        dto.setType(notif.getType());
        dto.setRead(notif.isRead());
        dto.setCreatedAt(notif.getCreatedAt());
        return dto;
    }
}
