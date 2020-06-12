package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

public class RegisterResponse {

    private StatusList registrationStatus;

    public StatusList getStatus() {
        return registrationStatus;
    }

    public void setStatus(StatusList registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
