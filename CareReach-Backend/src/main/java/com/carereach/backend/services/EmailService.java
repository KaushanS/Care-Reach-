package com.carereach.backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${carereach.mail.sender}")
    private String senderEmail;

    public void sendProvisioningEmail(String to, String fullName, String username, String tempPassword) {
        sendProvisioningEmail(to, fullName, username, tempPassword, "Administrator");
    }

    public void sendProvisioningEmail(String to, String fullName, String username, String tempPassword,
            String accountRole) {
        if (to == null)
            throw new IllegalArgumentException("Destination email cannot be null.");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Welcome to CareReach - Your " + accountRole + " Account");
            helper.setFrom(java.util.Objects.requireNonNull(senderEmail), "CareReach Official");

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
                        <div style="background-color: #fffbeb; padding: 15px; border-radius: 6px; border: 1px solid #fde68a;">
                            <p style="margin: 0; color: #b45309; font-size: 14px; font-weight: bold;">
                                Action Required: For your security, you will be forced to overwrite these credentials immediately upon your first login.
                            </p>
                        </div>
                        <p style="color: #94a3b8; font-size: 12px; margin-top: 30px; text-align: center;">
                            This is an automated system dispatch. Do not reply to this email.
                        </p>
                    </div>
                    """
                    .formatted(fullName, accountRole, username, tempPassword);

            helper.setText(java.util.Objects.requireNonNull(htmlContent), true); // true indicates HTML format

            mailSender.send(message);
            System.out.println("DEBUG: CareReach SMTP Payload dispatched successfully to " + to);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("FAILED TO DISPATCH SMTP: " + e.getMessage());
        } catch (org.springframework.mail.MailException e) {
            System.err.println("FAILED TO DISPATCH SENDER: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String to, String fullName, String accountRole) {
        if (to == null)
            throw new IllegalArgumentException("Destination email cannot be null.");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Welcome to CareReach Community!");
            helper.setFrom(java.util.Objects.requireNonNull(senderEmail), "CareReach Official");

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f8fafc; padding: 20px; border-radius: 8px; border-top: 5px solid #0f766e;">
                        <h2 style="color: #0f172a;">Welcome to CareReach, %s!</h2>
                        <p style="color: #475569; font-size: 15px; line-height: 1.5;">
                            Your <strong>%s</strong> account has been successfully created.
                        </p>
                        <p style="color: #475569; font-size: 15px; line-height: 1.5;">
                            Thank you for joining our platform. You can now log in using the credentials you provided during registration.
                        </p>
                        <p style="color: #94a3b8; font-size: 12px; margin-top: 30px; text-align: center;">
                            This is an automated system dispatch. Do not reply to this email.
                        </p>
                    </div>
                    """
                    .formatted(fullName, accountRole);

            helper.setText(java.util.Objects.requireNonNull(htmlContent), true);

            mailSender.send(message);
            System.out.println("DEBUG: CareReach Welcome Email dispatched successfully to " + to);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("FAILED TO DISPATCH SMTP: " + e.getMessage());
        } catch (org.springframework.mail.MailException e) {
            System.err.println("FAILED TO DISPATCH SENDER: " + e.getMessage());
        }
    }

    public void sendOtpVerificationEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            if (senderEmail != null)
                helper.setFrom(java.util.Objects.requireNonNull(senderEmail));
            helper.setTo(java.util.Objects.requireNonNull(toEmail));
            helper.setSubject("CareReach - Password Reset Verification Code");
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                    "<h2>Password Reset Verification</h2>" +
                    "<p>You requested to change your password. Use the verification code below to proceed:</p>" +
                    "<h1 style=\"color: #0f766e; letter-spacing: 5px;\">" + otpCode + "</h1>" +
                    "<p>This code will expire in 5 minutes. If you did not request this change, please ignore this email.</p></div>";
            helper.setText(java.util.Objects.requireNonNull(htmlContent), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}
