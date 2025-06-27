package com.jaya.sexoffenderapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaya.sexoffenderapp.model.OffenderRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OffenderService {

    public List<Map<String, Object>> searchOffenders(OffenderRequest request) {
        String apiUrl = "https://nsopw-api.ojp.gov/nsopw/v1/v1.0/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Origin", "https://www.nsopw.gov");
        headers.set("Referer", "https://www.nsopw.gov/");
        headers.set("User-Agent", "Mozilla/5.0");

        HttpEntity<OffenderRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        // Log raw JSON response for debugging
       // System.out.println("NSOPW Raw Response: " + response.getBody());

        return extractTopMatches(response.getBody(), request);
    }

    private List<Map<String, Object>> extractTopMatches(String responseJson, OffenderRequest request) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            JsonNode offenders = root.path("offenders");

            if (!offenders.isArray()) {
                System.out.println("No offenders found.");
                return resultList;
            }

            for (JsonNode offender : offenders) {
                int matchScore = 0;
                int total = 5;

                // Extract name
                JsonNode nameNode = offender.path("name");
                String firstName = nameNode.path("givenName").asText("");
                String lastName = nameNode.path("surName").asText("");

                // Extract location
                JsonNode locationNode = offender.path("locations").isArray() && offender.path("locations").size() > 0
                        ? offender.path("locations").get(0)
                        : null;

                String city = locationNode != null ? locationNode.path("city").asText("") : "";
                String state = locationNode != null ? locationNode.path("state").asText("") : "";

                // Extract other fields
                String dob = offender.path("dob").asText("");
                String gender = offender.path("gender").asText("");

                //  Match scoring
                if (firstName.equalsIgnoreCase(request.getFirstName())) matchScore++;
                if (lastName.equalsIgnoreCase(request.getLastName())) matchScore++;
                if (city.equalsIgnoreCase(request.getCity())) matchScore++;
                if (state.equalsIgnoreCase(request.getState())) matchScore++;
                if (dob.equalsIgnoreCase(request.getDob())) matchScore++;

                int percentage = (matchScore * 100) / total;

                // Constructing response map
                Map<String, Object> offenderMap = new HashMap<>();
                offenderMap.put("fullName", firstName + " " + lastName);
                offenderMap.put("dob", dob);
                offenderMap.put("gender", gender);
                offenderMap.put("city", city);
                offenderMap.put("state", state);
                offenderMap.put("percentageMatch", percentage);

                resultList.add(offenderMap);
            }

            // Sort by match score, return top 5
            resultList.sort((a, b) -> Integer.compare((int) b.get("percentageMatch"), (int) a.get("percentageMatch")));

            return resultList.stream().limit(5).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return resultList;
        }
    }
}
