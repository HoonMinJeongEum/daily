package com.ssafy.daily.reward.dto;

import com.ssafy.daily.user.entity.Member;
import lombok.Data;

@Data
public class ChildShellResponse {

    private final int memberId;

    private final String name;

    private final int shellCount;

    public ChildShellResponse(Member member, int shellCount) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.shellCount = shellCount;
    }
}
