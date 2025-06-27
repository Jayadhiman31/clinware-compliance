package com.jaya.clinwarecompliance.model;

import lombok.Data;
import java.util.List;

@Data 
public class OffenderRequest {

    private String firstName;    
    private String lastName;
    private String dob;
    private String city;
    private String state;
    private String country;
    private List<String> jurisdictions;
    private String clientIp;
}
