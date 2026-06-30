package com.iis.projekat.service;

import com.iis.projekat.dto.Campaign.CategoryDonationDTO;
import com.iis.projekat.dto.Campaign.DonationTrendDTO;
import com.iis.projekat.dto.Campaign.DonationTrendsReportDTO;
import com.iis.projekat.model.*;
import com.iis.projekat.repository.CampaignRepository;
import com.iis.projekat.repository.DonationRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Generates a global PDF report on gathered funds and donation trends across
 * all campaigns, including category statistics and the most successful
 * campaign (by total amount raised). Uses the OpenPDF library.
 */
@Service
public class DonationReportService {

    // Date formatting
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    // Colors
    private static final Color COLOR_HEADER_BG  = new Color(30, 77, 120);   // dark blue
    private static final Color COLOR_SECTION_BG = new Color(220, 234, 246); // light blue
    private static final Color COLOR_ROW_ALT    = new Color(245, 249, 253); // near-white
    private static final Color COLOR_WHITE      = Color.WHITE;
    private static final Color COLOR_TEXT       = new Color(33, 37, 41);

    // Fonts (BaseFont.HELVETICA works without external resources)
    private static Font fontTitle()   { return new Font(Font.HELVETICA, 20, Font.BOLD,   Color.WHITE); }
    private static Font fontSection() { return new Font(Font.HELVETICA, 12, Font.BOLD,   COLOR_HEADER_BG); }
    private static Font fontLabel()   { return new Font(Font.HELVETICA, 10, Font.BOLD,   COLOR_TEXT); }
    private static Font fontValue()   { return new Font(Font.HELVETICA, 10, Font.NORMAL, COLOR_TEXT); }
    private static Font fontSmall()   { return new Font(Font.HELVETICA,  8, Font.NORMAL, Color.GRAY); }
    private static Font fontTableHdr(){ return new Font(Font.HELVETICA, 10, Font.BOLD,   Color.WHITE); }
    private static Font fontTableRow(){ return new Font(Font.HELVETICA,  9, Font.NORMAL, COLOR_TEXT); }

    // Repositories
    private final CampaignRepository campaignRepository;
    private final DonationRepository donationRepository;

    public DonationReportService(CampaignRepository campaignRepository,
                                 DonationRepository donationRepository) {
        this.campaignRepository = campaignRepository;
        this.donationRepository = donationRepository;
    }

    //  Public API

    /**
     * Generates a PDF report on donation trends and campaign statistics
     * scoped to the given date range (inclusive).
     *
     * @param startDate start of the reporting period (inclusive)
     * @param endDate   end of the reporting period (inclusive)
     * @return byte array representing the PDF document
     */
    public byte[] generateReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid date range: startDate must be before or equal to endDate.");
        }
        DonationTrendsReportDTO dto = populateDTO(startDate, endDate);
        return generatePDF(dto, startDate, endDate);
    }

    //  Populating the DTO from the database

    private DonationTrendsReportDTO populateDTO(LocalDate startDate, LocalDate endDate) {
        DonationTrendsReportDTO dto = new DonationTrendsReportDTO();

        List<Campaign> campaigns = campaignRepository.findAll();

        // Donations within the reporting period, across all campaigns
        List<Donation> periodDonations = donationRepository.findAll().stream()
                .filter(d -> d.getPaymentDate() != null
                        && !d.getPaymentDate().isBefore(startDate)
                        && !d.getPaymentDate().isAfter(endDate))
                .toList();

        dto.totalRaised = periodDonations.stream()
                .mapToDouble(Donation::getAmount).sum();
        dto.totalDonations = periodDonations.size();
        dto.totalDonors = (int) periodDonations.stream()
                .filter(d -> d.getDonor() != null)
                .map(d -> d.getDonor().getId())
                .distinct().count();

        // Campaigns that received at least one donation in the period count
        // toward "total campaigns" for this report
        Set<Long> campaignIdsInPeriod = periodDonations.stream()
                .filter(d -> d.getCampaign() != null)
                .map(d -> d.getCampaign().getId())
                .collect(java.util.stream.Collectors.toSet());
        dto.totalCampaigns = campaignIdsInPeriod.size();

        // Monthly trends, zero-filled for months with no donations
        Map<YearMonth, Double> monthlyTotals = new TreeMap<>();
        YearMonth cursor = YearMonth.from(startDate);
        YearMonth lastMonth = YearMonth.from(endDate);
        while (!cursor.isAfter(lastMonth)) {
            monthlyTotals.put(cursor, 0.0);
            cursor = cursor.plusMonths(1);
        }
        for (Donation d : periodDonations) {
            YearMonth month = YearMonth.from(d.getPaymentDate());
            monthlyTotals.merge(month, d.getAmount(), Double::sum);
        }
        dto.trends = monthlyTotals.entrySet().stream()
                .map(e -> new DonationTrendDTO(e.getKey().toString(), e.getValue()))
                .toList();

        // Category breakdown within the period
        Map<CampaignCategory, Double> categoryTotals = periodDonations.stream()
                .filter(d -> d.getCampaign() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        d -> d.getCampaign().getCategory(),
                        java.util.stream.Collectors.summingDouble(Donation::getAmount)));
        dto.categoryBreakdown = categoryTotals.entrySet().stream()
                .map(e -> new CategoryDonationDTO(e.getKey(), e.getValue()))
                .toList();

        // Most successful campaign within the period, by total amount raised
        Campaign topCampaign = null;
        double topAmount = -1.0;
        List<Donation> topDonations = Collections.emptyList();

        Map<Long, List<Donation>> donationsByCampaign = periodDonations.stream()
                .filter(d -> d.getCampaign() != null)
                .collect(java.util.stream.Collectors.groupingBy(d -> d.getCampaign().getId()));

        for (Campaign campaign : campaigns) {
            List<Donation> campaignDonations = donationsByCampaign.getOrDefault(campaign.getId(), Collections.emptyList());
            double raised = campaignDonations.stream()
                    .mapToDouble(Donation::getAmount).sum();
            if (raised > topAmount) {
                topAmount = raised;
                topCampaign = campaign;
                topDonations = campaignDonations;
            }
        }

        if (topCampaign != null && topAmount > 0) {
            DonationTrendsReportDTO.TopCampaignDTO t = new DonationTrendsReportDTO.TopCampaignDTO();
            t.campaignId   = topCampaign.getId();
            t.name         = topCampaign.getName();
            t.category     = topCampaign.getCategory().name();
            t.status       = topCampaign.getStatus().name();
            t.goal         = topCampaign.getGoal();
            t.startDate    = topCampaign.getStartDate();
            t.endDate      = topCampaign.getEndDate();
            t.totalRaised  = topAmount;
            t.goalProgressPercent = t.goal != null && t.goal > 0
                    ? Math.round((t.totalRaised / t.goal) * 1000.0) / 10.0
                    : null;
            t.donationCount = topDonations.size();
            t.donorCount = (int) topDonations.stream()
                    .filter(d -> d.getDonor() != null)
                    .map(d -> d.getDonor().getId())
                    .distinct().count();
            dto.topCampaign = t;
        }

        return dto;
    }

    //  PDF generation

    private byte[] generatePDF(DonationTrendsReportDTO dto, LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Metadata
        doc.addTitle("Donation Trends Report");
        doc.addAuthor("Donation Management Subsystem");
        doc.addCreationDate();

        doc.open();

        addTitle(doc, startDate, endDate);
        addOverview(doc, dto);
        addDonationTrends(doc, dto);
        addCategoryBreakdown(doc, dto);
        addTopCampaign(doc, dto);

        doc.close();
        return baos.toByteArray();
    }

    //  Sections

    private void addTitle(Document doc, LocalDate startDate, LocalDate endDate) {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(20);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_HEADER_BG);
        cell.setPadding(20);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph title = new Paragraph("DONATION TRENDS REPORT", fontTitle());
        title.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(title);

        Paragraph subtitle = new Paragraph("Gathered Funds, Donation Trends & Campaign Statistics",
                new Font(Font.HELVETICA, 14, Font.BOLD, new Color(173, 216, 255)));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(6);
        cell.addElement(subtitle);

        Paragraph period = new Paragraph(
                "Period: " + startDate.format(DATE_FMT) + " – " + endDate.format(DATE_FMT),
                new Font(Font.HELVETICA, 10, Font.BOLD, new Color(200, 220, 240)));
        period.setAlignment(Element.ALIGN_CENTER);
        period.setSpacingBefore(6);
        cell.addElement(period);

        Paragraph date = new Paragraph(
                "Generated: " + java.time.LocalDate.now().format(DATE_FMT),
                new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(200, 220, 240)));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(4);
        cell.addElement(date);

        header.addCell(cell);
        doc.add(header);
    }

    private void addOverview(Document doc, DonationTrendsReportDTO dto) {
        addSectionHeader(doc, "1. OVERVIEW");

        PdfPTable table = newTable(new float[]{50f, 50f});
        table.setSpacingAfter(14);

        addRow(table, "Total amount raised",   String.format("%.2f USD", dto.totalRaised), false);
        addRow(table, "Total donations",       String.valueOf(dto.totalDonations), true);
        addRow(table, "Unique donors",         String.valueOf(dto.totalDonors), false);
        addRow(table, "Total campaigns",       String.valueOf(dto.totalCampaigns), true);

        doc.add(table);
    }

    private void addDonationTrends(Document doc, DonationTrendsReportDTO dto) {
        addSectionHeader(doc, "2. DONATION TRENDS BY MONTH");

        if (dto.trends == null || dto.trends.isEmpty()) {
            Paragraph p = new Paragraph("No donation data available.", fontValue());
            p.setSpacingAfter(14);
            doc.add(p);
            return;
        }

        try {
            Image chartImage = buildDonationTrendsChart(dto.trends);
            chartImage.setAlignment(Element.ALIGN_CENTER);
            chartImage.setSpacingAfter(14);
            doc.add(chartImage);
        } catch (DocumentException | IOException e) {
            // Fall back to a plain message if chart rendering fails for any reason
            Paragraph p = new Paragraph("Chart could not be rendered: " + e.getMessage(), fontSmall());
            p.setSpacingAfter(14);
            doc.add(p);
        }
    }

    /**
     * Builds a line chart of monthly donation totals using JFreeChart and
     * returns it as an embeddable OpenPDF Image.
     */
    private Image buildDonationTrendsChart(List<DonationTrendDTO> trends) throws DocumentException, IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DonationTrendDTO t : trends) {
            dataset.addValue(t.getAmount(), "Donations", t.getMonth());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                null,                 // no chart title (section header already serves this)
                "Month",
                "Amount (USD)",
                dataset,
                PlotOrientation.VERTICAL,
                false,                // no legend needed for a single series
                false,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(225, 230, 235));
        plot.setDomainGridlinesVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(true);

        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.UP_90);
        domainAxis.setTickLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, COLOR_HEADER_BG);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        byte[] pngBytes = org.jfree.chart.ChartUtilities.encodeAsPNG(chart.createBufferedImage(550, 330));
        Image image = Image.getInstance(pngBytes);
        image.scaleToFit(495f, 330f);
        return image;
    }

    private void addCategoryBreakdown(Document doc, DonationTrendsReportDTO dto) {
        doc.newPage();
        addSectionHeader(doc, "3. DONATIONS PER CATEGORY");

        if (dto.categoryBreakdown == null || dto.categoryBreakdown.isEmpty()) {
            Paragraph p = new Paragraph("No category data available.", fontValue());
            p.setSpacingAfter(14);
            doc.add(p);
            return;
        }

        PdfPTable table = newTable(new float[]{50f, 50f});
        table.setSpacingAfter(14);

        String[] header = {"Category", "Amount Raised (USD)"};
        for (String h : header) {
            PdfPCell c = new PdfPCell(new Phrase(h, fontTableHdr()));
            c.setBackgroundColor(COLOR_HEADER_BG);
            c.setPadding(6);
            table.addCell(c);
        }

        boolean alt = false;
        for (CategoryDonationDTO c : dto.categoryBreakdown) {
            Color bg = alt ? COLOR_ROW_ALT : COLOR_WHITE;
            addCell(table, categoryLabel(c.getCategory().name()), fontTableRow(), bg, 6);
            addCell(table, String.format("%.2f", c.getAmount()), fontTableRow(), bg, 6);
            alt = !alt;
        }
        doc.add(table);
    }

    private void addTopCampaign(Document doc, DonationTrendsReportDTO dto) {
        addSectionHeader(doc, "4. MOST SUCCESSFUL CAMPAIGN");

        if (dto.topCampaign == null) {
            Paragraph p = new Paragraph("No campaign data available.", fontValue());
            doc.add(p);
            return;
        }

        DonationTrendsReportDTO.TopCampaignDTO t = dto.topCampaign;

        PdfPTable table = newTable(new float[]{35f, 65f});
        table.setSpacingAfter(14);

        addRow(table, "Campaign name",   t.name, false);
        addRow(table, "Category",        categoryLabel(t.category), true);
        addRow(table, "Status",          statusLabel(t.status), false);
        addRow(table, "Start date",      fmt(t.startDate), true);
        addRow(table, "End date",        fmt(t.endDate), false);
        addRow(table, "Goal amount",     String.format("%.2f USD", t.goal != null ? t.goal : 0.0), true);
        addRow(table, "Amount raised",   String.format("%.2f USD", t.totalRaised), false);
        addRow(table, "Goal progress",
                t.goalProgressPercent != null
                        ? String.format("%.1f%%", t.goalProgressPercent)
                        : "–", true);
        addRow(table, "Total donations", String.valueOf(t.donationCount), false);
        addRow(table, "Unique donors",   String.valueOf(t.donorCount), true);

        doc.add(table);
    }


    //  Formatting helpers

    private void addSectionHeader(Document doc, String text) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(14);
        t.setSpacingAfter(6);

        PdfPCell c = new PdfPCell(new Phrase(text, fontSection()));
        c.setBackgroundColor(COLOR_SECTION_BG);
        c.setPadding(8);
        c.setBorderColor(COLOR_HEADER_BG);
        c.setBorderWidth(1);
        t.addCell(c);
        doc.add(t);
    }

    private PdfPTable newTable(float[] widths) {
        PdfPTable t = new PdfPTable(widths);
        t.setWidthPercentage(100);
        return t;
    }

    private void addRow(PdfPTable table, String label, String value, boolean alt) {
        Color bg = alt ? COLOR_ROW_ALT : COLOR_WHITE;

        PdfPCell lCell = new PdfPCell(new Phrase(label, fontLabel()));
        lCell.setBackgroundColor(bg);
        lCell.setPadding(6);
        lCell.setBorderColor(new Color(210, 218, 227));
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "–", fontValue()));
        vCell.setBackgroundColor(bg);
        vCell.setPadding(6);
        vCell.setBorderColor(new Color(210, 218, 227));
        table.addCell(vCell);
    }

    private void addCell(PdfPTable t, String text, Font font, Color bg, float padding) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBackgroundColor(bg);
        c.setPadding(padding);
        c.setBorderColor(new Color(210, 218, 227));
        t.addCell(c);
    }

    private String fmt(java.time.LocalDate d) {
        return d != null ? d.format(DATE_FMT) : "–";
    }

    private String statusLabel(String status) {
        if (status == null) return "–";
        return switch (status) {
            case "PLANNED"  -> "Planned";
            case "ACTIVE"   -> "Active";
            case "FINISHED" -> "Finished";
            default -> status;
        };
    }

    private String categoryLabel(String category) {
        if (category == null) return "–";
        return switch (category) {
            case "FOOD_AID"          -> "Food Aid";
            case "MEDICAL"           -> "Medical";
            case "EDUCATION"         -> "Education";
            case "SHELTER"           -> "Shelter";
            case "DISASTER_RELIEF"   -> "Disaster Relief";
            case "COMMUNITY_SUPPORT" -> "Community Support";
            default -> category;
        };
    }
}