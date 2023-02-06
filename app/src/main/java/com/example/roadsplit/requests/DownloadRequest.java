package com.example.roadsplit.requests;

public class DownloadRequest {
    private String filename;
    private long reiseId;

    public DownloadRequest(String filename, long reiseId) {
        this.filename = filename;
        this.reiseId = reiseId;
    }

    public DownloadRequest() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getReiseId() {
        return reiseId;
    }

    public void setReiseId(long reiseId) {
        this.reiseId = reiseId;
    }
}
