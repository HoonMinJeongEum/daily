package com.ssafy.daily.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuyStickerRequest {
    int stickerId; // 스티커 고유 번호
}
