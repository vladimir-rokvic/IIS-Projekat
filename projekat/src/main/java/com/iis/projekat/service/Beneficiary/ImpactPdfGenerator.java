package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.ImpactReportDto;
import com.iis.projekat.dto.Beneficiary.SurveyCommentDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ImpactPdfGenerator {

    private static final Color HEADER = new Color(30, 77, 120);
    private static final Color TEXT = new Color(33, 37, 41);

    private final ImpactReportService impactReportService;

    private static Font titleFont() {
        return new Font(Font.HELVETICA, 18, Font.BOLD, HEADER);
    }

    private static Font sectionFont() {
        return new Font(Font.HELVETICA, 12, Font.BOLD, HEADER);
    }

    private static Font textFont() {
        return new Font(Font.HELVETICA, 10, Font.NORMAL, TEXT);
    }

    public byte[] generate() {
        ImpactReportDto dto = impactReportService.generate();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(document, out);

            document.open();

            addTitle(document);

            addOverview(document, dto);
            addRatingDistribution(document, dto);
            addLatestComments(document, dto);
            addRatingPerDistribution(document, dto);
            addRatingPerAidType(document, dto);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate impact report", e);
        }
    }

    // =========================
    // TITLE
    // =========================
    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Impact Report", titleFont());
        title.setSpacingAfter(15);
        document.add(title);
    }

    // =========================
    // OVERVIEW
    // =========================
    private void addOverview(Document document, ImpactReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Overview", sectionFont());
        section.setSpacingBefore(10);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addRow(table, "Total surveys", String.valueOf(dto.getTotalSurveys()));
        addRow(table, "Total packages", String.valueOf(dto.getTotalPackages()));
        addRow(table, "Response rate", String.format("%.2f%%", dto.getResponseRate()));
        addRow(table, "Average rating", String.format("%.2f", dto.getAverageRating()));

        document.add(table);
    }

    // =========================
    // RATING DISTRIBUTION
    // =========================
    private void addRatingDistribution(Document document, ImpactReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Rating distribution", sectionFont());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addHeader(table, "Rating");
        addHeader(table, "Count");

        dto.getRatingDistribution().forEach((rating, count) -> {
            table.addCell(rating + " ★");
            table.addCell(String.valueOf(count));
        });

        document.add(table);
    }

    // =========================
    // LATEST COMMENTS
    // =========================
    private void addLatestComments(Document document, ImpactReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Latest comments", sectionFont());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 6f, 2f});

        addHeader(table, "Distribution");
        addHeader(table, "Comment");
        addHeader(table, "Rating");

        for (SurveyCommentDto c : dto.getLatestComments()) {

            table.addCell(String.valueOf(c.distributionId()));
            table.addCell(c.comment() != null ? c.comment() : "-");
            table.addCell(String.valueOf(c.rating()));
        }

        document.add(table);
    }

    // =========================
    // RATING PER DISTRIBUTION
    // =========================
    private void addRatingPerDistribution(Document document, ImpactReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Rating per distribution", sectionFont());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addHeader(table, "Distribution ID");
        addHeader(table, "Avg rating");

        dto.getAverageRatingPerDistribution().forEach((id, avg) -> {
            table.addCell(String.valueOf(id));
            table.addCell(String.format("%.2f", avg));
        });

        document.add(table);
    }

    // =========================
    // RATING PER AID TYPE
    // =========================
    private void addRatingPerAidType(Document document, ImpactReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph("Rating per Aid Type", sectionFont());
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addHeader(table, "Aid Type");
        addHeader(table, "Avg rating");

        dto.getAverageRatingPerAidType().forEach((type, avg) -> {
            table.addCell(type.name());
            table.addCell(String.format("%.2f", avg));
        });

        document.add(table);
    }

    // =========================
    // HELPERS
    // =========================
    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, textFont()));
        cell.setBackgroundColor(HEADER);
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String label, String value) {
        table.addCell(new Phrase(label, textFont()));
        table.addCell(new Phrase(value, textFont()));
    }
}