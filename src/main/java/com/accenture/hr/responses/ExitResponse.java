package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

public class ExitResponse {

    private StatusList status;

    public StatusList getStatus() {
        return status;
    }

    public void setStatus(StatusList status) {
        this.status = status;
    }
}
