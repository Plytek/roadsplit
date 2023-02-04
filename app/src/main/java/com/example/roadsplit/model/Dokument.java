package com.example.roadsplit.model;

public class Dokument {
    private long id;
    private String encodedImage;
    private String fileType;
    private String fileName;

    public Dokument(long id, String encodedImage, String fileType, String fileName) {
        this.id = id;
        this.encodedImage = encodedImage;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public Dokument(String encodedImage, String fileType, String fileName) {
        this.encodedImage = encodedImage;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public Dokument() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
