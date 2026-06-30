package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.DistributionVolunteerCountDto;
import com.iis.projekat.dto.Beneficiary.EfficiencyReportDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class EfficiencyPdfGenerator {

    private final EfficiencyReportService efficiencyReportService;

    private static com.lowagie.text.Font fontTableHdr(){ return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD,   Color.WHITE); }
    private static final Color COLOR_HEADER_BG  = new Color(30, 77, 120);
    private static final Color COLOR_TEXT       = new Color(33, 37, 41);

    private static com.lowagie.text.Font fontTitle() {
        return new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                18,
                com.lowagie.text.Font.BOLD,
                COLOR_HEADER_BG
        );
    }

    private static com.lowagie.text.Font fontSection() {
        return new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                12,
                com.lowagie.text.Font.BOLD,
                COLOR_HEADER_BG
        );
    }

    private static com.lowagie.text.Font fontText() {
        return new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                10,
                com.lowagie.text.Font.NORMAL,
                COLOR_TEXT
        );
    }

    public byte[] generate() {
        EfficiencyReportDto dto = efficiencyReportService.generate();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(document, out);

            document.open();

            addTitle(document);
            addGeneralStats(document, dto);
            addStatusBreakdown(document, dto);
            addLocationBreakdown(document, dto);
            addAverageStats(document, dto);
            addVolunteerStats(document, dto);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate efficiency report PDF", e);
        }
    }

    // =========================
    // TITLE
    // =========================
    private void addTitle(Document document) throws DocumentException {

        Paragraph title = new Paragraph("Efficiency Report", fontTitle());
        title.setSpacingAfter(15);

        document.add(title);
    }

    // =========================
    // GENERAL STATS
    // =========================
    private void addGeneralStats(Document document, EfficiencyReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("General statistics", fontSection());
        section.setSpacingBefore(10);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Total distributions",
                String.valueOf(dto.getTotalDistributions()));

        document.add(table);
    }

    // =========================
    // STATUS BREAKDOWN
    // =========================
    private void addStatusBreakdown(Document document, EfficiencyReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Distributions by status", fontSection());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addHeader(table, "Status");
        addHeader(table, "Count");

        dto.getDistributionsByStatus().forEach((status, count) -> {
            table.addCell(status.name());
            table.addCell(String.valueOf(count));
        });

        document.add(table);
    }

    // =========================
    // LOCATION BREAKDOWN
    // =========================
    private void addLocationBreakdown(Document document, EfficiencyReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Distributions by location", fontSection());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addHeader(table, "Location");
        addHeader(table, "Count");

        dto.getDistributionsByLocation().forEach((loc, count) -> {
            table.addCell(loc);
            table.addCell(String.valueOf(count));
        });

        document.add(table);
    }

    // =========================
    // AVERAGES
    // =========================
    private void addAverageStats(Document document, EfficiencyReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Average metrics", fontSection());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Avg beneficiaries per distribution",
                String.format("%.2f", dto.getAvgBeneficiariesPerDistribution()));

        addRow(table, "Avg packages per distribution",
                String.format("%.2f", dto.getAvgPackagesPerDistribution()));

        document.add(table);
    }

    // =========================
    // VOLUNTEERS
    // =========================
    private void addVolunteerStats(Document document, EfficiencyReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Volunteers per distribution", fontSection());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 5f, 3f, 2f});

        addHeader(table, "Distribution ID");
        addHeader(table, "Note");
        addHeader(table, "Date");
        addHeader(table, "Volunteer Count");

        for (DistributionVolunteerCountDto d : dto.getVolunteersPerDistribution()) {
            table.addCell(d.distributionId().toString());
            table.addCell(d.note());
            table.addCell(d.scheduledDate().toString());
            table.addCell(d.volunteerCount().toString());
        }

        document.add(table);
    }

    // =========================
    // HELPERS
    // =========================
    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, fontTableHdr()));
        cell.setBackgroundColor(COLOR_HEADER_BG);
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String label, String value) {
        table.addCell(new Phrase(label, fontText()));
        table.addCell(new Phrase(value, fontText()));
    }
}
