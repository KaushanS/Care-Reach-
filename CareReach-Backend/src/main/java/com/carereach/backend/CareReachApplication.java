package com.carereach.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.ApplicationRunner;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class CareReachApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Colombo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CareReachApplication.class, args);
    }

    @Bean
    @SuppressWarnings("null")
    public ApplicationRunner seedData(com.carereach.backend.repositories.NotificationRepository notifRepo,
            com.carereach.backend.repositories.UserRepository userRepo) {
        return args -> {
            // Seed Default Admin Account
            if (!userRepo.findByUsername("admin").isPresent()) {
                com.carereach.backend.models.User admin = new com.carereach.backend.models.User();
                admin.setFullName("System Administrator");
                admin.setUsername("admin");
                admin.setEmail("admin@carereach.gov.lk");
                admin.setPassword("admin123");
                admin.setRole(com.carereach.backend.models.Role.ADMIN);
                userRepo.save(admin);
                System.out.println("✅ Seeded default Admin account (username: admin, password: admin123)");
            }

            if (notifRepo.count() == 0) {
                userRepo.findAll().forEach(user -> {
                    com.carereach.backend.models.Notification n1 = new com.carereach.backend.models.Notification();
                    n1.setTargetUser(user);
                    n1.setMessage("Welcome to CareReach! Your volunteer account is fully activated.");
                    n1.setType("SUCCESS");

                    com.carereach.backend.models.Notification n2 = new com.carereach.backend.models.Notification();
                    n2.setTargetUser(user);
                    n2.setMessage("Your recent patient report has been logged successfully.");
                    n2.setType("INFO");

                    com.carereach.backend.models.Notification n3 = new com.carereach.backend.models.Notification();
                    n3.setTargetUser(user);
                    n3.setMessage("Urgent Warning: Expected severe storms in your assigned District.");
                    n3.setType("WARNING");

                    com.carereach.backend.models.Notification n4 = new com.carereach.backend.models.Notification();
                    n4.setTargetUser(user);
                    n4.setMessage("New CareReach Global System architecture update v1.2.0 deployed.");
                    n4.setType("INFO");

                    com.carereach.backend.models.Notification n5 = new com.carereach.backend.models.Notification();
                    n5.setTargetUser(user);
                    n5.setMessage("A Regional Officer verified your latest medical report.");
                    n5.setType("SUCCESS");

                    com.carereach.backend.models.Notification n6 = new com.carereach.backend.models.Notification();

                    n6.setTargetUser(user);
                    n6.setMessage("Admin: Please update your grama niladhari mapping.");
                    n6.setType("ALERT");
                    n6.setRead(true);

                    Iterable<com.carereach.backend.models.Notification> notifications = java.util.List.of(n1, n2, n3,
                            n4, n5, n6);
                    notifRepo.saveAll(notifications);
                });
                System.out.println("✅ Seeded dummy notifications for testing!");
            }
        };
    }
}
