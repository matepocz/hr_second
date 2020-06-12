package com.accenture.hr.responses;

import com.accenture.hr.enums.StatusList;

public class RegisterResponse {

    private StatusList registrationStatus;

    public StatusList getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(StatusList registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
