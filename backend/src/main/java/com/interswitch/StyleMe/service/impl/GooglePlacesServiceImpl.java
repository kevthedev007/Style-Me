package com.interswitch.StyleMe.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.StyleMe.dto.responses.PlaceResponse;
import com.interswitch.StyleMe.service.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GooglePlacesServiceImpl implements GooglePlacesService {

    @Value("${google.maps.api.key}")
    private String googleApiKey;
    @Value("${latitude}")
    private double LATITUDE;
    @Value("${longitude}")
    private double LONGITUDE;

    @Override
    public List<PlaceResponse> findLaundry(int radius) {
        String placesApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(placesApiUrl)
                .queryParam("location", LATITUDE + "," + LONGITUDE)
                .queryParam("radius", radius)
                .queryParam("type", "laundry")
                .queryParam("key", googleApiKey);

        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(builder.toUriString(), String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode results = jsonResponse.get("results");

            List<PlaceResponse> placeResponses = new ArrayList<>();

            for (JsonNode result : results) {
                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setName(result.get("name").asText());
                placeResponse.setAddress(result.get("vicinity").asText());
                placeResponse.setRating(result.has("rating") ? result.get("rating").asDouble() : 0.0);
                placeResponse.setUserRatingsTotal(result.has("user_ratings_total") ? result.get("user_ratings_total").asInt() : 0);
                placeResponse.setBusinessStatus(result.has("business_status") ? result.get("business_status").asText() : "Unknown");

                String openingHoursStatus = "Unknown";
                if (result.has("opening_hours") && result.get("opening_hours").has("open_now")) {
                    boolean openNow = result.get("opening_hours").get("open_now").asBoolean();
                    openingHoursStatus = openNow ? "Open Now" : "Closed";
                }
                placeResponse.setOpeningHoursStatus(openingHoursStatus);

                placeResponses.add(placeResponse);
            }

            return placeResponses;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
