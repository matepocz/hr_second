package com.accenture.hr.responses;

import com.accenture.hr.enums.Status;

import java.net.URL;

public class EntryResponse {

    private Status status;
    private URL url;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
