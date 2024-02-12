package com.interswitch.StyleMe.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RecommendationResponseDto {
    private List<List<String>> recommendations;
}
