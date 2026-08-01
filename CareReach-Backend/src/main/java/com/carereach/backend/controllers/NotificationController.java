package com.carereach.backend.controllers;

import com.carereach.backend.dtos.NotificationDto;
import com.carereach.backend.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<Map<String, Object>> getLatestNotifications(@PathVariable Long userId) {
        List<NotificationDto> list = notificationService.getLatestNotifications(userId);
        long unread = notificationService.getUnreadCount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", list);
        response.put("unreadCount", unread);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
