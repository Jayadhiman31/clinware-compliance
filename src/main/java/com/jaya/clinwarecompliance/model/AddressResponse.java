package com.jaya.clinwarecompliance.model;

public class AddressResponse {
    private boolean isValid;
    private StandardizedAddress standardizedAddress;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public StandardizedAddress getStandardizedAddress() {
        return standardizedAddress;
    }

    public void setStandardizedAddress(StandardizedAddress standardizedAddress) {
        this.standardizedAddress = standardizedAddress;
    }
}
