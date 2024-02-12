package com.interswitch.StyleMe.service;

import com.interswitch.StyleMe.dto.responses.PlaceResponse;

import java.util.List;

public interface GooglePlacesService {
    List<PlaceResponse> findLaundry(int radius);
}
