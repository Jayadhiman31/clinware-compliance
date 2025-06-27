package com.jaya.clinwarecompliance.model;


import lombok.Data;

@Data
public class AddressRequest {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
}
