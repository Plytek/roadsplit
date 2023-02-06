package com.example.roadsplit.requests;

import com.example.roadsplit.model.Dokument;
import com.example.roadsplit.model.Reise;
import com.example.roadsplit.model.Reisender;

public class UploadRequest {
    private Dokument dokument;
    private Reise reise;

    private Reisender reisender;

    public UploadRequest(Dokument dokument, Reise reise, Reisender reisender) {
        this.dokument = dokument;
        this.reise = reise;
        this.reisender = reisender;
    }

    public UploadRequest(Dokument dokument, Reise reise) {
        this.dokument = dokument;
        this.reise = reise;
    }

    public UploadRequest() {
    }

    public Dokument getDokument() {
        return dokument;
    }

    public void setDokument(Dokument dokument) {
        this.dokument = dokument;
    }

    public Reise getReise() {
        return reise;
    }

    public void setReise(Reise reise) {
        this.reise = reise;
    }
}
