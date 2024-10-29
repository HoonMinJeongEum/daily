package com.ssafy.daily.user.dto;

import com.ssafy.daily.user.entity.Member;
import lombok.Data;

@Data
public class ProfilesResponse {
    private int id;
    private String name;
    private String img;

    public ProfilesResponse(Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
    }
}
