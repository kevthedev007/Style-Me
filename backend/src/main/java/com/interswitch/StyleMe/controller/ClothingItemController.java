package com.interswitch.StyleMe.controller;

import com.interswitch.StyleMe.dto.responses.OutfitResponseDto;
import com.interswitch.StyleMe.dto.responses.RecommendationResponseDto;
import com.interswitch.StyleMe.enums.EventType;
import com.interswitch.StyleMe.exceptions.StyleMeException;
import com.interswitch.StyleMe.model.ClothingItem;
import com.interswitch.StyleMe.service.OutfitService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/outfits")
@Tag(name = "ClothingItemController", description = "CRUD REST APIs For Outfits")
@RequiredArgsConstructor
public class ClothingItemController {

    private final OutfitService outfitService;

    @Operation(summary = "Upload Outfit REST API", description = "Upload Outfit REST API is used to upload outfit to " +
            "Google Drive and save the information in the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadOutfit(@RequestParam("file") MultipartFile file,
                                               @RequestParam("itemCategory") String itemCategory,
                                               @RequestParam("eventTypes") List<String> eventTypes
    ) throws StyleMeException {
        List<EventType> eventTypeList = eventTypes.stream()
                .map(EventType::valueOf)
                .collect(Collectors.toList());

        return ResponseEntity.ok(outfitService.uploadOutfit(file, itemCategory, eventTypeList));
    }

    @Operation(summary = "Delete Outfit REST API", description = "Delete Outfit REST API is used to delete outfit in " +
            "Google Drive and in the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteOutfit(@PathVariable String fileId) throws StyleMeException {
        return ResponseEntity.ok(outfitService.deleteFile(fileId));
    }

    @Operation(summary = "Get Outfits By Events REST API", description = "Get Outfits REST API is used to get all the outfits " +
            "by events in the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{event}")
    public ResponseEntity<List<OutfitResponseDto>> getOutfitsByEvent(@PathVariable String event) throws StyleMeException {
        return ResponseEntity.ok(outfitService.getOutfitsByEvent(event));
    }

    @Operation(summary = "Recommend Outfit REST API", description = "Recommend Outfit REST API is used to recommend " +
            "outfit to the user according to the specified event")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/recommend/{event}")
    public ResponseEntity<RecommendationResponseDto> recommendOutfit(@PathVariable String event) throws StyleMeException {
        return ResponseEntity.ok(outfitService.recommendOutfit(event));
    }

    @PutMapping("/{clothId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCloth(@PathVariable String clothId) throws StyleMeException {
        outfitService.markClothForSale(clothId);
    }

    @GetMapping("/forsale")
    public ResponseEntity<?> getClothsForSale() {
        return ResponseEntity.ok(outfitService.getUsersAndClothingForSale());
    }
}
