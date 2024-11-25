package com.ssafy.daily.user.dto;

import com.ssafy.daily.user.entity.Member;
import lombok.Data;

@Data
public class ProfilesResponse {
    private int id;
    private String name;
    private String img;
    private int shellCount;

    public ProfilesResponse(Member member, int shellCount){
        this.id = member.getId();
        this.name = member.getName();
        this.img = member.getImg();
        this.shellCount = shellCount;
    }
}
