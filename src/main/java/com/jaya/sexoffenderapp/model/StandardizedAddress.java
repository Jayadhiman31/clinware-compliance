package com.jaya.sexoffenderapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardizedAddress {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
}
