package com.interswitch.StyleMe.service;

import com.interswitch.StyleMe.dto.UserClothingDTO;
import com.interswitch.StyleMe.dto.responses.OutfitResponseDto;
import com.interswitch.StyleMe.dto.responses.RecommendationResponseDto;
import com.interswitch.StyleMe.enums.EventType;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.ClothingItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface OutfitService {
    String uploadOutfit(MultipartFile file, String itemCategory, List<EventType> eventTypes) throws StyleMeException;
    String deleteFile(String fileId) throws StyleMeException;

    List<OutfitResponseDto> getOutfitsByEvent(String event) throws StyleMeException;

    void markClothForSale(String clothId) throws StyleMeException;
    RecommendationResponseDto recommendOutfit(String eventType) throws StyleMeException;

    List<UserClothingDTO> getUsersAndClothingForSale();
}
