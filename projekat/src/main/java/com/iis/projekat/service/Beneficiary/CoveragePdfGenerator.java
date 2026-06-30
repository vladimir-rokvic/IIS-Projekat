package com.iis.projekat.service.Beneficiary;

import com.iis.projekat.dto.Beneficiary.CoverageReportDto;
import com.iis.projekat.model.Beneficiary.AidType;
import com.iis.projekat.model.Beneficiary.DistributionStatus;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoveragePdfGenerator {
    // Formatiranje datuma
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    // Boje
    private static final Color COLOR_HEADER_BG  = new Color(30, 77, 120);   // tamno plava
    private static final Color COLOR_SECTION_BG = new Color(220, 234, 246); // svetlo plava
    private static final Color COLOR_ROW_ALT    = new Color(245, 249, 253); // gotovo bela
    private static final Color COLOR_WHITE      = Color.WHITE;
    private static final Color COLOR_TEXT       = new Color(33, 37, 41);

    // Fontovi (BaseFont.HELVETICA radi bez eksternih resursa)
    private static com.lowagie.text.Font fontTitle()   { return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 20, com.lowagie.text.Font.BOLD,   Color.WHITE); }
    private static com.lowagie.text.Font fontSection() { return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD,   COLOR_HEADER_BG); }
    private static com.lowagie.text.Font fontLabel()   { return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD,   COLOR_TEXT); }
    private static com.lowagie.text.Font fontValue()   { return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL, COLOR_TEXT); }
    private static com.lowagie.text.Font fontSmall()   { return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA,  8, com.lowagie.text.Font.NORMAL, Color.GRAY); }
    private static com.lowagie.text.Font fontTableHdr(){ return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD,   Color.WHITE); }
    private static com.lowagie.text.Font fontTableRow(){ return new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA,  9, Font.NORMAL, COLOR_TEXT); }

    private final CoverageReportService coverageReportService;

    public byte[] generateCoverageReport(String period) {

        CoverageReportDto dto = coverageReportService.generate(period);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 54, 36);

            PdfWriter.getInstance(document, out);

            document.open();

            addTitle(document, dto);

            addGeneralStats(document, dto);

            addAidTypeTable(document, dto);

            addDistributionStatusTable(document, dto);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addTitle(Document document, CoverageReportDto dto)
            throws DocumentException {

        Paragraph title = new Paragraph(
                "Coverage Report",
                fontSection()
        );

        title.setSpacingAfter(20);

        document.add(title);

        document.add(new Paragraph(
                "Period: " + dto.getPeriod(),
                fontValue()
        ));
    }

    private void addGeneralStats(Document document, CoverageReportDto dto)
            throws DocumentException {

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100);
        table.setSpacingBefore(15);

        addRow(table,
                "Total eligible beneficiaries",
                String.valueOf(dto.getTotalEligibleBeneficiaries()));

        addRow(table,
                "Received aid",
                String.valueOf(dto.getBeneficiariesReceivedAid()));

        addRow(table,
                "Coverage",
                String.format("%.2f%%", dto.getCoveragePercentage()));

        document.add(table);
    }

    private void addAidTypeTable(Document document, CoverageReportDto dto)
            throws DocumentException {

        Paragraph section = new Paragraph(
                "Breakdown by Aid Type",
                fontSection()
        );

        section.setSpacingBefore(20);
        section.setSpacingAfter(10);

        document.add(section);

        PdfPTable table = new PdfPTable(3);

        table.setWidthPercentage(100);

        addHeader(table, "Aid Type");
        addHeader(table, "Count");
        addHeader(table,"Percentage");
        float sum = 0f;
        for (Map.Entry<AidType, Long> entry :
                dto.getAidTypeBreakdown().entrySet()) {
            sum+=entry.getValue().floatValue();
        }

        for (Map.Entry<AidType, Long> entry :
                dto.getAidTypeBreakdown().entrySet()) {

            table.addCell(entry.getKey().name());
            table.addCell(entry.getValue().toString());
            float percentage = 100*entry.getValue().floatValue()/sum;
            table.addCell(String.format("%.2f%%",percentage));
        }

        document.add(table);
    }

    private void addDistributionStatusTable(
            Document document,
            CoverageReportDto dto
    ) throws DocumentException {

        Paragraph section = new Paragraph(
                "Distributions by Status",
                fontSection()
        );

        section.setSpacingBefore(20);
        section.setSpacingAfter(10);

        document.add(section);

        PdfPTable table = new PdfPTable(2);

        table.setWidthPercentage(100);

        addHeader(table, "Status");
        addHeader(table, "Count");

        for (Map.Entry<DistributionStatus, Long> entry :
                dto.getDistributionsByStatus().entrySet()) {

            table.addCell(entry.getKey().name());
            table.addCell(entry.getValue().toString());
        }

        document.add(table);
    }

    private void addHeader(PdfPTable table, String text) {

        PdfPCell cell = new PdfPCell(new Phrase(text, fontTableHdr()));

        cell.setBackgroundColor(COLOR_HEADER_BG);

        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String label, String value) {

        table.addCell(new Phrase(label, fontLabel()));
        table.addCell(new Phrase(value, fontValue()));
    }

}
