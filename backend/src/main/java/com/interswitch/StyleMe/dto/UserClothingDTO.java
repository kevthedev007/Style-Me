package com.interswitch.StyleMe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserClothingDTO {
    private UserDto user;
    private List<ClothDto> clothingItemsForSale;
}
