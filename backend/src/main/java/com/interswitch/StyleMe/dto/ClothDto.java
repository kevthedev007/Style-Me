package com.interswitch.StyleMe.dto;

import com.interswitch.StyleMe.enums.EventType;
import com.interswitch.StyleMe.enums.ItemCategory;
import com.interswitch.StyleMe.model.ClothingItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ClothDto {

    private String url;

    private ItemCategory itemCategory;

    private List<EventType> event;


    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private boolean forSale;

    public static List<ClothDto> clothingItemDTO(List<ClothingItem> clothingItem) {
        return clothingItem.stream().map(clothingItem1 -> {
            ClothDto clothDto = new ClothDto();
            BeanUtils.copyProperties(clothingItem1, clothDto);
            return clothDto;
        }).collect(Collectors.toList());

    }

}


