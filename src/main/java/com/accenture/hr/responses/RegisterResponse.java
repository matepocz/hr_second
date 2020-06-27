package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

import java.net.URL;

public class RegisterResponse {

    private StatusList registrationStatus;
    private URL url;

    public StatusList getStatus() {
        return registrationStatus;
    }

    public void setStatus(StatusList registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
