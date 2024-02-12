package com.interswitch.StyleMe.dto.responses;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ApiResponse {
    private String status;
    private Object data;
}
