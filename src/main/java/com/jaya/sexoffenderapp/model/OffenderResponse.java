package com.jaya.sexoffenderapp.model;

import lombok.Data;
import java.util.List;

@Data
public class OffenderResponse {
    private String fullName;
    private String dob;
    private String gender;
    private String city;
    private String state;
   
}