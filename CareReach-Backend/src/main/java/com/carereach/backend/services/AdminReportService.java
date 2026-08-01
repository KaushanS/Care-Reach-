package com.carereach.backend.services;

import com.carereach.backend.models.Patient;
import com.carereach.backend.models.User;
import com.carereach.backend.models.Role;
import com.carereach.backend.repositories.PatientRepository;
import com.carereach.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public byte[] generatePatientCsv() {
        List<Patient> patients = patientRepository.findAll();
        StringBuilder csv = new StringBuilder("Patient ID,Full Name,NIC,Gender,District,Status,Registered At\n");
        for (Patient p : patients) {
            String name = escapeCsv(p.getName());
            String nic = escapeCsv(p.getNic());
            String gender = escapeCsv(p.getGender());
            String district = escapeCsv(p.getDistrict());

            csv.append(p.getId()).append(",")
                    .append(name).append(",")
                    .append(nic).append(",")
                    .append(gender).append(",")
                    .append(district).append(",")
                    .append(p.getStatus()).append(",")
                    .append(p.getCreatedAt()).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generateOfficerCsv() {
        List<User> officers = userRepository.findByRole(Role.OFFICER);
        StringBuilder csv = new StringBuilder("Officer ID,Full Name,Email,Phone,GN Division,District,Registered At\n");
        for (User u : officers) {
            csv.append(u.getId()).append(",")
                    .append(escapeCsv(u.getFullName())).append(",")
                    .append(escapeCsv(u.getEmail())).append(",")
                    .append(escapeCsv(u.getPhone())).append(",")
                    .append(escapeCsv(u.getGnDivision())).append(",")
                    .append(escapeCsv(u.getDistrict())).append(",")
                    .append(u.getCreatedAt()).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generateUserAuditCsv() {
        List<User> users = userRepository.findAll();
        StringBuilder csv = new StringBuilder("User ID,Role,Full Name,Email,Phone,Joined At\n");
        for (User u : users) {
            csv.append(u.getId()).append(",")
                    .append(u.getRole()).append(",")
                    .append(escapeCsv(u.getFullName())).append(",")
                    .append(escapeCsv(u.getEmail())).append(",")
                    .append(escapeCsv(u.getPhone())).append(",")
                    .append(u.getCreatedAt()).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generateEmergencyPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("CareReach Emergency Incident Logs", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 3f, 3f, 2f, 2f });
            table.setSpacingBefore(10);

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = { "ID", "Patient Name", "Reported By", "District", "Status" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, headFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }

            
            List<Patient> emergencies = patientRepository.findAll();

            for (Patient p : emergencies) {
                table.addCell(String.valueOf(p.getId()));
                table.addCell(p.getName() != null ? p.getName() : "N/A");

                String reporter = "System";
                if (p.getReportedBy() != null) {
                    reporter = p.getReportedBy().getFullName();
                }
                table.addCell(reporter);
                table.addCell(p.getDistrict() != null ? p.getDistrict() : "Unknown");
                table.addCell(p.getStatus() != null ? p.getStatus() : "");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Emergency PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateEmergencyCsv() {
        List<Patient> emergencies = patientRepository.findAll();
        StringBuilder csv = new StringBuilder("ID,Patient Name,Reported By,District,Status\n");
        for (Patient p : emergencies) {
            String reporter = "System";
            if (p.getReportedBy() != null) {
                reporter = p.getReportedBy().getFullName();
            }
            csv.append(p.getId()).append(",")
                    .append(escapeCsv(p.getName() != null ? p.getName() : "N/A")).append(",")
                    .append(escapeCsv(reporter)).append(",")
                    .append(escapeCsv(p.getDistrict() != null ? p.getDistrict() : "Unknown")).append(",")
                    .append(escapeCsv(p.getStatus() != null ? p.getStatus() : "")).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generatePatientPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("CareReach Patient Demographics", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 2f, 2f, 1f, 2f, 1.5f, 2f });
            table.setSpacingBefore(10);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = { "ID", "Name", "NIC", "Gender", "District", "Status", "Registered At" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, headFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }
            List<Patient> patients = patientRepository.findAll();
            for (Patient p : patients) {
                table.addCell(String.valueOf(p.getId()));
                table.addCell(p.getName() != null ? p.getName() : "");
                table.addCell(p.getNic() != null ? p.getNic() : "");
                table.addCell(p.getGender() != null ? p.getGender() : "");
                table.addCell(p.getDistrict() != null ? p.getDistrict() : "");
                table.addCell(p.getStatus() != null ? p.getStatus() : "");
                table.addCell(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Patient PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateOfficerPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("CareReach GN Officer Network", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 2f, 2.5f, 1.5f, 1.5f, 1.5f, 2f });
            table.setSpacingBefore(10);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = { "ID", "Name", "Email", "Phone", "GN Division", "District", "Registered" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, headFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }
            List<User> officers = userRepository.findByRole(Role.OFFICER);
            for (User u : officers) {
                table.addCell(String.valueOf(u.getId()));
                table.addCell(u.getFullName() != null ? u.getFullName() : "");
                table.addCell(u.getEmail() != null ? u.getEmail() : "");
                table.addCell(u.getPhone() != null ? u.getPhone() : "");
                table.addCell(u.getGnDivision() != null ? u.getGnDivision() : "");
                table.addCell(u.getDistrict() != null ? u.getDistrict() : "");
                table.addCell(u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Officer PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateUserAuditPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("CareReach User Audit Log", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 1f, 1.5f, 2f, 2.5f, 1.5f, 2f });
            table.setSpacingBefore(10);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = { "ID", "Role", "Name", "Email", "Phone", "Joined" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, headFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6);
                table.addCell(cell);
            }
            List<User> users = userRepository.findAll();
            for (User u : users) {
                table.addCell(String.valueOf(u.getId()));
                table.addCell(u.getRole() != null ? u.getRole().name() : "");
                table.addCell(u.getFullName() != null ? u.getFullName() : "");
                table.addCell(u.getEmail() != null ? u.getEmail() : "");
                table.addCell(u.getPhone() != null ? u.getPhone() : "");
                table.addCell(u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating User Audit PDF", e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
