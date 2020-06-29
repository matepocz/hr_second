package com.accenture.hr.responses;

import com.accenture.hr.enums.Status;

import java.net.URL;

public class RegisterResponse {

    private Status status;
    private URL url;
    private int positionInQueue;

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

    public int getPositionInQueue() {
        return positionInQueue;
    }

    public void setPositionInQueue(int positionInQueue) {
        this.positionInQueue = positionInQueue;
    }
}
