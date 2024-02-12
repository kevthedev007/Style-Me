package com.interswitch.StyleMe.dto.responses;

import com.interswitch.StyleMe.enums.ItemCategory;
import lombok.Data;

@Data
public class OutfitResponseDto {
    private String url;
    private String driveId;
    private String event;
    private ItemCategory itemCategory;
    private Boolean forSale;
}
