package com.customer360.backend.export;

import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.dto.OrderResponse;
import com.customer360.backend.service.CustomerConsolidationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerExportService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerExportService.class);

    private final CustomerConsolidationService customerConsolidationService;

    public CustomerExportService(CustomerConsolidationService customerConsolidationService) {
        this.customerConsolidationService = customerConsolidationService;
    }

    public String exportCustomersToCsv() {
        List<CustomerDetailResponse> customers = getCustomersForExport();

        String[] headers = getExportHeaders();
        StringBuilder csv = new StringBuilder();

        csv.append(String.join(",", headers)).append("\n");

        for (CustomerDetailResponse customer : customers) {
            csv.append(escapeCsv(customer.getCustomerId())).append(",");
            csv.append(escapeCsv(customer.getName())).append(",");
            csv.append(escapeCsv(customer.getEmail())).append(",");
            csv.append(escapeCsv(customer.getMobile())).append(",");
            csv.append(escapeCsv(customer.getCity())).append(",");
            csv.append(escapeCsv(customer.getGender())).append(",");
            csv.append(escapeCsv(safe(customer.getAge()))).append(",");
            csv.append(escapeCsv(customer.getDateOfBirth())).append(",");
            csv.append(escapeCsv(customer.getAddress())).append(",");
            csv.append(escapeCsv(customer.getCustomerType())).append(",");
            csv.append(escapeCsv(customer.getMembership())).append(",");
            csv.append(escapeCsv(customer.getPreferredChannel())).append(",");
            csv.append(escapeCsv(customer.getPreferredLanguage())).append(",");
            csv.append(escapeCsv(formatBoolean(customer.getNotificationOptIn()))).append(",");
            csv.append(escapeCsv(formatBoolean(customer.getMarketingConsent()))).append(",");
            csv.append(escapeCsv(customer.getPreferredContactTime())).append(",");
            csv.append(customer.getTotalOrders()).append(",");
            csv.append(safeDecimal(customer.getTotalOrderAmount())).append(",");
            csv.append(safeDecimal(customer.getTotalDiscountAmount())).append(",");
            csv.append(safeDecimal(customer.getNetOrderAmount())).append(",");
            csv.append(customer.getRewardPoints()).append(",");
            csv.append(escapeCsv(customer.getCustomerSegment())).append(",");
            csv.append(customer.getDataQualityScore()).append(",");
            csv.append(escapeCsv(customer.getDataQualityStatus())).append(",");
            csv.append(escapeCsv(joinOrderValues(customer, OrderResponse::getProductCategory))).append(",");
            csv.append(escapeCsv(joinOrderValues(customer, OrderResponse::getOrderStatus))).append(",");
            csv.append(escapeCsv(joinOrderValues(customer, OrderResponse::getPaymentMode))).append(",");
            csv.append(escapeCsv(String.join(" | ", customer.getWarnings()))).append("\n");
        }

        return csv.toString();
    }

    public byte[] exportCustomersToExcel() {
        try {
            List<CustomerDetailResponse> customers = getCustomersForExport();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Customers");

            String[] headers = getExportHeaders();

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (CustomerDetailResponse customer : customers) {
                Row row = sheet.createRow(rowIndex++);

                int column = 0;

                row.createCell(column++).setCellValue(safe(customer.getCustomerId()));
                row.createCell(column++).setCellValue(safe(customer.getName()));
                row.createCell(column++).setCellValue(safe(customer.getEmail()));
                row.createCell(column++).setCellValue(safe(customer.getMobile()));
                row.createCell(column++).setCellValue(safe(customer.getCity()));
                row.createCell(column++).setCellValue(safe(customer.getGender()));
                row.createCell(column++).setCellValue(customer.getAge() != null ? customer.getAge() : 0);
                row.createCell(column++).setCellValue(safe(customer.getDateOfBirth()));
                row.createCell(column++).setCellValue(safe(customer.getAddress()));
                row.createCell(column++).setCellValue(safe(customer.getCustomerType()));
                row.createCell(column++).setCellValue(safe(customer.getMembership()));
                row.createCell(column++).setCellValue(safe(customer.getPreferredChannel()));
                row.createCell(column++).setCellValue(safe(customer.getPreferredLanguage()));
                row.createCell(column++).setCellValue(formatBoolean(customer.getNotificationOptIn()));
                row.createCell(column++).setCellValue(formatBoolean(customer.getMarketingConsent()));
                row.createCell(column++).setCellValue(safe(customer.getPreferredContactTime()));
                row.createCell(column++).setCellValue(customer.getTotalOrders());
                row.createCell(column++).setCellValue(safeDecimal(customer.getTotalOrderAmount()).doubleValue());
                row.createCell(column++).setCellValue(safeDecimal(customer.getTotalDiscountAmount()).doubleValue());
                row.createCell(column++).setCellValue(safeDecimal(customer.getNetOrderAmount()).doubleValue());
                row.createCell(column++).setCellValue(customer.getRewardPoints());
                row.createCell(column++).setCellValue(safe(customer.getCustomerSegment()));
                row.createCell(column++).setCellValue(customer.getDataQualityScore());
                row.createCell(column++).setCellValue(safe(customer.getDataQualityStatus()));
                row.createCell(column++).setCellValue(joinOrderValues(customer, OrderResponse::getProductCategory));
                row.createCell(column++).setCellValue(joinOrderValues(customer, OrderResponse::getOrderStatus));
                row.createCell(column++).setCellValue(joinOrderValues(customer, OrderResponse::getPaymentMode));
                row.createCell(column).setCellValue(String.join(" | ", customer.getWarnings()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception ex) {
            logger.error("Failed to export customers to Excel", ex);
            throw new RuntimeException("Failed to export customers to Excel", ex);
        }
    }

    public byte[] exportCustomersToPdf() {
        try {
            List<CustomerDetailResponse> customers = getCustomersForExport();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A3.rotate());
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Customer360 Enhanced Export Report", titleFont);
            title.setSpacingAfter(12);
            document.add(title);

            String[] pdfHeaders = {
                    "Customer ID",
                    "Name",
                    "City",
                    "Gender",
                    "Age",
                    "Type",
                    "Membership",
                    "Channel",
                    "Language",
                    "Orders",
                    "Total",
                    "Discount",
                    "Net",
                    "Reward",
                    "Segment",
                    "Quality",
                    "Warnings"
            };

            PdfPTable table = new PdfPTable(pdfHeaders.length);
            table.setWidthPercentage(100);

            for (String header : pdfHeaders) {
                addPdfHeader(table, header);
            }

            for (CustomerDetailResponse customer : customers) {
                addPdfCell(table, customer.getCustomerId());
                addPdfCell(table, customer.getName());
                addPdfCell(table, customer.getCity());
                addPdfCell(table, customer.getGender());
                addPdfCell(table, safe(customer.getAge()));
                addPdfCell(table, customer.getCustomerType());
                addPdfCell(table, customer.getMembership());
                addPdfCell(table, customer.getPreferredChannel());
                addPdfCell(table, customer.getPreferredLanguage());
                addPdfCell(table, String.valueOf(customer.getTotalOrders()));
                addPdfCell(table, String.valueOf(safeDecimal(customer.getTotalOrderAmount())));
                addPdfCell(table, String.valueOf(safeDecimal(customer.getTotalDiscountAmount())));
                addPdfCell(table, String.valueOf(safeDecimal(customer.getNetOrderAmount())));
                addPdfCell(table, String.valueOf(customer.getRewardPoints()));
                addPdfCell(table, customer.getCustomerSegment());
                addPdfCell(table, customer.getDataQualityScore() + " - " + safe(customer.getDataQualityStatus()));
                addPdfCell(table, String.join(" | ", customer.getWarnings()));
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception ex) {
            logger.error("Failed to export customers to PDF", ex);
            throw new RuntimeException("Failed to export customers to PDF", ex);
        }
    }

    private List<CustomerDetailResponse> getCustomersForExport() {
        return customerConsolidationService.getAllCustomers(
                null,
                null,
                null,
                null,
                "customerId",
                "asc"
        );
    }

    private String[] getExportHeaders() {
        return new String[]{
                "Customer ID",
                "Name",
                "Email",
                "Mobile",
                "City",
                "Gender",
                "Age",
                "Date of Birth",
                "Address",
                "Customer Type",
                "Membership",
                "Preferred Channel",
                "Preferred Language",
                "Notification Opt-In",
                "Marketing Consent",
                "Preferred Contact Time",
                "Total Orders",
                "Total Order Amount",
                "Total Discount Amount",
                "Net Order Amount",
                "Reward Points",
                "Customer Segment",
                "Data Quality Score",
                "Data Quality Status",
                "Product Categories",
                "Order Statuses",
                "Payment Modes",
                "Warnings"
        };
    }

    private void addPdfHeader(PdfPTable table, String headerText) {
        Font font = new Font(Font.HELVETICA, 6, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(headerText, font));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addPdfCell(PdfPTable table, String value) {
        Font font = new Font(Font.HELVETICA, 6, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(safe(value), font));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private String joinOrderValues(CustomerDetailResponse customer, Function<OrderResponse, String> extractor) {
        if (customer.getOrders() == null || customer.getOrders().isEmpty()) {
            return "Not Available";
        }

        Set<String> values = customer.getOrders()
                .stream()
                .map(extractor)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (values.isEmpty()) {
            return "Not Available";
        }

        return String.join(" | ", values);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.replace("\"", "\"\"");

        if (escapedValue.contains(",") || escapedValue.contains("\"") || escapedValue.contains("\n")) {
            return "\"" + escapedValue + "\"";
        }

        return escapedValue;
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "Not Available";
        }

        return value;
    }

    private String safe(Integer value) {
        if (value == null) {
            return "Not Available";
        }

        return String.valueOf(value);
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatBoolean(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Yes" : "No";
    }
}
