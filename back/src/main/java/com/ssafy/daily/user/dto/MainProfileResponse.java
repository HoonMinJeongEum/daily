package com.ssafy.daily.user.dto;

import com.ssafy.daily.reward.entity.Quest;
import com.ssafy.daily.user.entity.Member;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MainProfileResponse {
    private int memberId;
    private String img;
    private Boolean diaryStatus;
    private Boolean quizStatus;
    private Boolean wordStatus;
    private int shellCount;

    // 생성자
    public MainProfileResponse(Member member, Quest quest, int shellCount) {
        this.memberId = member.getId();
        this.img = member.getImg();
        this.diaryStatus = quest.isDiaryStatus();
        this.quizStatus = quest.isQuizStatus();
        this.wordStatus = quest.isWordStatus();
        this.shellCount = shellCount;
    }
}
