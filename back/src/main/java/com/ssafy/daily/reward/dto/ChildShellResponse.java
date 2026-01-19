package com.ssafy.daily.reward.dto;

import lombok.Data;

@Data
public class ChildShellResponse {

    private final int memberId;

    private final String name;

    private final int shellCount;

    public ChildShellResponse(int memberId, String name, int shellCount) {
        this.memberId = memberId;
        this.name = name;
        this.shellCount = shellCount;
    }
}
