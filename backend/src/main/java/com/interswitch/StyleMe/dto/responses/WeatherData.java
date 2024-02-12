package com.interswitch.StyleMe.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherData {
    @JsonProperty("name")
    private String cityName;

    @JsonProperty("main")
    private MainInfo mainInfo;

    @JsonProperty("weather")
    private WeatherInfo[] weatherInfo;

    public static class MainInfo {
        @JsonProperty("temp")
        private double temperature;

        @JsonProperty("humidity")
        private double humidity;
    }

    public static class WeatherInfo {
        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;
    }
}
