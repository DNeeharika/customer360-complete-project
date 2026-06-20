package com.customer360.backend.dto;

import java.time.LocalDateTime;

public class DataUploadResponse {

    private String dataType;
    private String sourceName;
    private int loadedRows;
    private int skippedRows;
    private String message;
    private LocalDateTime uploadedAt;

    public DataUploadResponse() {
    }

    public DataUploadResponse(
            String dataType,
            String sourceName,
            int loadedRows,
            int skippedRows,
            String message
    ) {
        this.dataType = dataType;
        this.sourceName = sourceName;
        this.loadedRows = loadedRows;
        this.skippedRows = skippedRows;
        this.message = message;
        this.uploadedAt = LocalDateTime.now();
    }

    public String getDataType() {
        return dataType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getLoadedRows() {
        return loadedRows;
    }

    public int getSkippedRows() {
        return skippedRows;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setLoadedRows(int loadedRows) {
        this.loadedRows = loadedRows;
    }

    public void setSkippedRows(int skippedRows) {
        this.skippedRows = skippedRows;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
