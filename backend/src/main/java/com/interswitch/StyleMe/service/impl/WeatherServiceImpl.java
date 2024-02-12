package com.interswitch.StyleMe.service.impl;

import com.interswitch.StyleMe.dto.responses.WeatherData;
import com.interswitch.StyleMe.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@RequiredArgsConstructor
@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${openweather.api.key}")
    private String apiKey;
    @Value("${latitude}")
    private double LATITUDE;
    @Value("${longitude}")
    private double LONGITUDE;
    @Override
    public WeatherData getWeatherByCoordinates() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + LATITUDE + "&lon=" + LONGITUDE + "&appid=" + apiKey + "&units=metric";

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(apiUrl, WeatherData.class);
    }
}
