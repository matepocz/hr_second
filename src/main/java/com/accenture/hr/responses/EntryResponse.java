package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

public class EntryResponse {

    private StatusList status;

    public StatusList getStatus() {
        return status;
    }

    public void setStatus(StatusList status) {
        this.status = status;
    }
}
