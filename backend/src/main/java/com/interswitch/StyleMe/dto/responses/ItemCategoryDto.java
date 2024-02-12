package com.interswitch.StyleMe.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ItemCategoryDto {
    private List<String> trousers;
    private List<String> skirts;
    private List<String> shoes;
    private List<String> shirts;
    private List<String> dresses;
    private List<String> bags;
    private List<String> apparels;
}
