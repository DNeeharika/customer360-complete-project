package com.customer360.backend.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.customer360.backend.dto.CustomerDetailResponse;
import com.customer360.backend.service.CustomerConsolidationService;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class CustomerExportService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerExportService.class);

    private final CustomerConsolidationService customerConsolidationService;

    public CustomerExportService(CustomerConsolidationService customerConsolidationService) {
        this.customerConsolidationService = customerConsolidationService;
    }

    public String exportCustomersToCsv() {

        List<CustomerDetailResponse> customers = getCustomersForExport();

        StringBuilder csv = new StringBuilder();

        csv.append("Customer ID,Name,Email,Mobile,City,Membership,Preferred Channel,Total Orders,Total Order Amount,Warnings\n");

        for (CustomerDetailResponse customer : customers) {
            csv.append(escapeCsv(customer.getCustomerId())).append(",");
            csv.append(escapeCsv(customer.getName())).append(",");
            csv.append(escapeCsv(customer.getEmail())).append(",");
            csv.append(escapeCsv(customer.getMobile())).append(",");
            csv.append(escapeCsv(customer.getCity())).append(",");
            csv.append(escapeCsv(customer.getMembership())).append(",");
            csv.append(escapeCsv(customer.getPreferredChannel())).append(",");
            csv.append(customer.getTotalOrders()).append(",");
            csv.append(customer.getTotalOrderAmount()).append(",");
            csv.append(escapeCsv(String.join(" | ", customer.getWarnings()))).append("\n");
        }

        return csv.toString();
    }

    public byte[] exportCustomersToExcel() {
        try {
            List<CustomerDetailResponse> customers = getCustomersForExport();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Customers");

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Customer ID",
                    "Name",
                    "Email",
                    "Mobile",
                    "City",
                    "Membership",
                    "Preferred Channel",
                    "Total Orders",
                    "Total Order Amount",
                    "Warnings"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (CustomerDetailResponse customer : customers) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(customer.getCustomerId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getMobile());
                row.createCell(4).setCellValue(customer.getCity());
                row.createCell(5).setCellValue(customer.getMembership());
                row.createCell(6).setCellValue(customer.getPreferredChannel());
                row.createCell(7).setCellValue(customer.getTotalOrders());
                row.createCell(8).setCellValue(customer.getTotalOrderAmount().doubleValue());
                row.createCell(9).setCellValue(String.join(" | ", customer.getWarnings()));
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

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Customer 360 Export Report", titleFont);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);

            addPdfHeader(table, "Customer ID");
            addPdfHeader(table, "Name");
            addPdfHeader(table, "Email");
            addPdfHeader(table, "Mobile");
            addPdfHeader(table, "City");
            addPdfHeader(table, "Membership");
            addPdfHeader(table, "Channel");
            addPdfHeader(table, "Orders");
            addPdfHeader(table, "Amount");

            for (CustomerDetailResponse customer : customers) {
                table.addCell(customer.getCustomerId());
                table.addCell(customer.getName());
                table.addCell(customer.getEmail());
                table.addCell(customer.getMobile());
                table.addCell(customer.getCity());
                table.addCell(customer.getMembership());
                table.addCell(customer.getPreferredChannel());
                table.addCell(String.valueOf(customer.getTotalOrders()));
                table.addCell(String.valueOf(customer.getTotalOrderAmount()));
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

    private void addPdfHeader(PdfPTable table, String headerText) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(headerText, font));
        table.addCell(cell);
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
}