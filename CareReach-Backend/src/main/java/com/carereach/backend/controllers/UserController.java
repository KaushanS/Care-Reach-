package com.carereach.backend.controllers;

import com.carereach.backend.dtos.PasswordUpdateDto;
import com.carereach.backend.dtos.UserProfileDto;
import com.carereach.backend.dtos.UserProfileUpdateDto;
import com.carereach.backend.dtos.UserRegistrationDto;
import com.carereach.backend.models.User;
import com.carereach.backend.services.UserService;
import com.carereach.backend.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User registeredUser = userService.registerUser(registrationDto);
            return ResponseEntity.ok().body("User registered successfully with ID: " + registeredUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody com.carereach.backend.dtos.LoginDto loginDto) {
        try {
            User user = userService.authenticateUser(loginDto.getUsername(), loginDto.getPassword());
            java.util.Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("id", user.getId());
            responseData.put("role", user.getRole().name());
            responseData.put("fullName", user.getFullName());
            responseData.put("username", user.getUsername());
            responseData.put("profilePhoto", user.getProfilePhoto());
            responseData.put("requiresCredentialReset", user.isRequiresCredentialReset());
            return ResponseEntity.ok(responseData);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/force-reset")
    public ResponseEntity<?> forceResetCredentials(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        try {
            boolean success = userService.forceResetCredentials(id, body.get("newUsername"), body.get("newPassword"));
            if (success) {
                return ResponseEntity.ok().body("Credentials secured successfully.");
            }
            return ResponseEntity.badRequest().body("Failed to reset credentials.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileDto profile = userService.getUserProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long id, @RequestBody UserProfileUpdateDto profileDto) {
        try {
            UserProfileDto updatedProfile = userService.updateUserProfile(id, profileDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/request-password-otp")
    public ResponseEntity<?> requestPasswordResetOtp(@PathVariable Long id,
            @RequestBody PasswordUpdateDto passwordDto) {
        try {
            userService.requestPasswordResetOtp(id, passwordDto.getCurrentPassword());
            return ResponseEntity.ok().body("OTP sent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @RequestBody PasswordUpdateDto passwordDto) {
        try {
            userService.updateUserPassword(id, passwordDto);
            return ResponseEntity.ok().body("Password updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file);
            userService.updateProfilePhoto(id, url);
            return ResponseEntity.ok(java.util.Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
