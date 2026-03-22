package com.example.flexapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlexBalanceResponse {

    private Long userId;
    private Integer totalFlexMinutes;
}
