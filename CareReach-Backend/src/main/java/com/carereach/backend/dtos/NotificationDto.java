package com.carereach.backend.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
