package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.Sticker;
import lombok.Data;

@Data
public class EarnedStickerResponse {

    private final int id;

    private final String img;

    public EarnedStickerResponse(Sticker sticker) {
        this.id = sticker.getId();
        this.img = sticker.getImg();
    }
}
