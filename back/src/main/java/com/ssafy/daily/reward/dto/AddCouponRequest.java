package com.ssafy.daily.reward.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCouponRequest {

    @NotBlank(message = "쿠폰 설명을 입력해 주세요.")
    private String description;

    @NotNull(message = "쿠폰 가격을 입력해 주세요.")
    @Min(value = 1, message = "쿠폰 가격은 1 이상이어야 합니다.")
    private Integer price;
}
