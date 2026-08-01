package com.carereach.backend.dtos;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String currentPassword;
    private String newPassword;
    private String otpCode;
}
