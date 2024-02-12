package com.interswitch.StyleMe.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResponse {
    private String name;
    private String address;
    private double rating;
    private int userRatingsTotal;
    private String businessStatus;
    private String openingHoursStatus;
}
