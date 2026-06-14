package com.customer360.backend.controller;

import com.customer360.backend.export.CustomerExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Customer Export APIs", description = "APIs for exporting consolidated customer data")
public class CustomerExportController {

    private final CustomerExportService customerExportService;

    public CustomerExportController(CustomerExportService customerExportService) {
        this.customerExportService = customerExportService;
    }

    @GetMapping("/api/customers/export/csv")
    @Operation(
            summary = "Export customers as CSV",
            description = "Exports consolidated customer data in CSV format."
    )
    public ResponseEntity<String> exportCustomersToCsv() {
        String csvData = customerExportService.exportCustomersToCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }

    @GetMapping("/api/customers/export/excel")
    @Operation(
            summary = "Export customers as Excel",
            description = "Exports consolidated customer data in Excel XLSX format."
    )
    public ResponseEntity<byte[]> exportCustomersToExcel() {
        byte[] excelData = customerExportService.exportCustomersToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/api/customers/export/pdf")
    @Operation(
            summary = "Export customers as PDF",
            description = "Exports consolidated customer data in PDF format."
    )
    public ResponseEntity<byte[]> exportCustomersToPdf() {
        byte[] pdfData = customerExportService.exportCustomersToPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
