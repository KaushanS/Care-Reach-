package com.carereach.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Value("${carereach.mail.sender}")
    private String senderEmail;

    @Value("${spring.mail.password}")
    private String apiKey;
    
    @Autowired
    private ObjectMapper objectMapper;

    private void sendBrevoEmailAsync(String to, String subject, String htmlContent) {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> payload = Map.of(
                        "sender", Map.of("name", "CareReach Official", "email", senderEmail),
                        "to", List.of(Map.of("email", to)),
                        "subject", subject,
                        "htmlContent", htmlContent
                );
                
                String jsonBody = objectMapper.writeValueAsString(payload);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                        .header("accept", "application/json")
                        .header("api-key", apiKey)
                        .header("content-type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                System.out.println("DEBUG: Dispatching async HTTP email payload to Brevo...");
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("DEBUG: Brevo HTTP Response: " + response.statusCode() + " " + response.body());
            } catch (Exception e) {
                System.err.println("FAILED TO DISPATCH BREVO HTTP: " + e.getMessage());
            }
        });
    }

    public void sendProvisioningEmail(String to, String fullName, String username, String tempPassword) {
        sendProvisioningEmail(to, fullName, username, tempPassword, "Administrator");
    }

    public void sendProvisioningEmail(String to, String fullName, String username, String tempPassword, String accountRole) {
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f8fafc; padding: 20px; border-radius: 8px; border-top: 5px solid #0f766e;">
                    <h2 style="color: #0f172a;">Welcome to CareReach, %s!</h2>
                    <p style="color: #475569; font-size: 15px; line-height: 1.5;">
                        Your CareReach <strong>%s</strong> dashboard access has been securely provisioned.
                        Please find your credentials below.
                    </p>
                    <div style="background-color: #ffffff; padding: 15px; border-radius: 6px; border: 1px solid #cbd5e1; margin: 20px 0;">
                        <p style="margin: 0 0 10px 0; color: #0f172a;"><strong>Username:</strong> <span style="color: #0ea5e9;">%s</span></p>
                        <p style="margin: 0; color: #0f172a;"><strong>Password:</strong> <span style="background-color: #f1f5f9; padding: 4px 8px; border-radius: 4px; font-family: monospace;">%s</span></p>
                    </div>
                </div>
                """.formatted(fullName, accountRole, username, tempPassword);
        sendBrevoEmailAsync(to, "Welcome to CareReach - Your " + accountRole + " Account", htmlContent);
    }

    public void sendWelcomeEmail(String to, String fullName, String accountRole) {
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f8fafc; padding: 20px; border-radius: 8px; border-top: 5px solid #0f766e;">
                    <h2 style="color: #0f172a;">Welcome to CareReach, %s!</h2>
                    <p style="color: #475569; font-size: 15px; line-height: 1.5;">
                        Your <strong>%s</strong> account has been successfully created.
                    </p>
                </div>
                """.formatted(fullName, accountRole);
        sendBrevoEmailAsync(to, "Welcome to CareReach Community!", htmlContent);
    }

    public void sendOtpVerificationEmail(String to, String otpCode) {
        String htmlContent = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                "<h2>Password Reset Verification</h2>" +
                "<p>You requested to change your password. Use the verification code below to proceed:</p>" +
                "<h1 style=\"color: #0f766e; letter-spacing: 5px;\">" + otpCode + "</h1>" +
                "<p>This code will expire in 5 minutes.</p></div>";
        sendBrevoEmailAsync(to, "CareReach - Password Reset Verification Code", htmlContent);
    }
}
