package com.interswitch.StyleMe.service;

import com.interswitch.StyleMe.dto.responses.WeatherData;

public interface WeatherService {
    WeatherData getWeatherByCoordinates();
}
