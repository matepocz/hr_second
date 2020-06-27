package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

import java.net.URL;

public class EntryResponse {

    private StatusList status;
    private URL url;

    public StatusList getStatus() {
        return status;
    }

    public void setStatus(StatusList status) {
        this.status = status;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
