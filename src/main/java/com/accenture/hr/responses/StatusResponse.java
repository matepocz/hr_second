package com.accenture.hr.responses;

import com.accenture.hr.enums.Status;

public class StatusResponse {

    private Status status;
    private int positionInQueue;

    public Status getStatus() {
        return status;
    }

    public int getPositionInQueue() {
        return positionInQueue;
    }

    public void setPositionInQueue(int positionInQueue) {
        this.positionInQueue = positionInQueue;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
