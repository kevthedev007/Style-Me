package com.interswitch.StyleMe.controller;

import com.interswitch.StyleMe.dto.responses.WeatherData;
import com.interswitch.StyleMe.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    @GetMapping()
    public ResponseEntity<WeatherData> getWeatherByCoordinates() {
        WeatherData weatherData = weatherService.getWeatherByCoordinates();

        if (weatherData != null) {
            return ResponseEntity.ok(weatherData);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
