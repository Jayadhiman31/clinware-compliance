package com.jaya.clinwarecompliance.controller;

import com.jaya.clinwarecompliance.model.AddressRequest;
import com.jaya.clinwarecompliance.model.AddressResponse;
import com.jaya.clinwarecompliance.service.AddressValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<AddressResponse> validate(@RequestBody AddressRequest request) {
        return ResponseEntity.ok(validationService.validateAddress(request));
    }
}
