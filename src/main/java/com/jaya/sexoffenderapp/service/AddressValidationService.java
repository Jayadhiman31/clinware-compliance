package com.jaya.sexoffenderapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaya.sexoffenderapp.model.AddressRequest;
import com.jaya.sexoffenderapp.model.AddressResponse;
import com.jaya.sexoffenderapp.model.StandardizedAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddressValidationService {

    @Value("${google.api.key.path}")
    private String apiKeyPath;

    private final RestTemplate restTemplate = new RestTemplate();

    public AddressResponse validateAddress(AddressRequest request) {
        AddressResponse response = new AddressResponse();

        try {
            // Read API key from file
            String apiKey = Files.readString(Paths.get(apiKeyPath)).trim();

            // Google API endpoint
            String apiUrl = "https://addressvalidation.googleapis.com/v1:validateAddress?key=" + apiKey;

            // Prepare payload
            Map<String, Object> address = new HashMap<>();
            address.put("addressLines", new String[]{request.getAddressLine1()});
            address.put("regionCode", "US");
            address.put("administrativeArea", request.getState());
            address.put("locality", request.getCity());
            address.put("postalCode", request.getZipCode());

            Map<String, Object> payload = new HashMap<>();
            payload.put("address", address);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Call the API
            ResponseEntity<String> apiResponse = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // Log the raw API response
            System.out.println("Raw response from API: " + apiResponse.getBody());

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(apiResponse.getBody());
            JsonNode result = root.path("result");
            JsonNode verdict = result.path("verdict");

            String granularity = verdict.path("validationGranularity").asText();
            boolean isValid = granularity.equals("PREMISE") || granularity.equals("SUB_PREMISE") || granularity.equals("ROUTE");
            response.setValid(isValid);

            // Extract standardized address if available
            JsonNode postalAddress = result.path("address").path("postalAddress");
            if (postalAddress != null && postalAddress.has("addressLines")) {
                StandardizedAddress stdAddress = new StandardizedAddress();
                stdAddress.setAddressLine1(postalAddress.path("addressLines").get(0).asText(""));
                stdAddress.setAddressLine2(request.getAddressLine2());
                stdAddress.setCity(postalAddress.path("locality").asText(""));
                stdAddress.setState(postalAddress.path("administrativeArea").asText(""));
                stdAddress.setZipCode(postalAddress.path("postalCode").asText(""));

                response.setStandardizedAddress(stdAddress);
            } else {
                response.setStandardizedAddress(null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.setValid(false);
            response.setStandardizedAddress(null);
        }

        return response;
    }
}
