package com.ssafy.daily.reward.dto;

import com.ssafy.daily.reward.entity.Sticker;
import lombok.Getter;

@Getter
public class StickerResponse {

    private final int id;

    private final String img;

    private final int price;

    public StickerResponse(Sticker sticker) {
        this.id = sticker.getId();
        this.img = sticker.getImg();
        this.price = sticker.getPrice();
    }
}
