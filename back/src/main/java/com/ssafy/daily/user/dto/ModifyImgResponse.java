package com.ssafy.daily.user.dto;

import lombok.Data;

@Data
public class ModifyImgResponse {
    private String img;
    public ModifyImgResponse(String img){
        this.img = img;
    }
}
