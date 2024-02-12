package com.interswitch.StyleMe.controller;

import com.interswitch.StyleMe.dto.responses.PlaceResponse;
import com.interswitch.StyleMe.service.GooglePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maps")
@RequiredArgsConstructor
public class MapController {

    private final GooglePlacesService googlePlacesService;

    @GetMapping("/findLaundry")
    public ResponseEntity<List<PlaceResponse>> findLaundry(
            @RequestParam("radius") int radius) {

        List<PlaceResponse> placeResponses = googlePlacesService.findLaundry(radius);

        if (placeResponses.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(placeResponses);
    }
}
