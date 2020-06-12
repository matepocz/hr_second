package com.accenture.hr.responses;

import com.accenture.hr.enums.RegistrationStatus;

public class RegisterResponse {

    private RegistrationStatus registrationStatus;

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
