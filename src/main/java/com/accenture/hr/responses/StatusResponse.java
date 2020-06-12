package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

public class StatusResponse {

    private StatusList status;
    private int positionInQueue;

    public StatusList getStatus() {
        return status;
    }

    public int getPositionInQueue() {
        return positionInQueue;
    }

    public void setPositionInQueue(int positionInQueue) {
        this.positionInQueue = positionInQueue;
    }

    public void setStatus(StatusList status) {
        this.status = status;
    }
}
