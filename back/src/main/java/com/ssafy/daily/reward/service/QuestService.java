package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.reward.dto.UpdateQuestRequest;
import com.ssafy.daily.reward.entity.Quest;
import com.ssafy.daily.reward.repository.QuestRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class QuestService {
    private final QuestRepository questRepository;
    private final ShellService shellService;
    private final MemberRepository memberRepository;

    /**
     * 퀘스트 완료
     */
    public StatusResponse updateQuest(CustomUserDetails userDetails, UpdateQuestRequest request) {
        Member member = shellService.validateMember(userDetails.getMember().getId());
        Quest quest = questRepository.findByMemberId(member.getId());

        // 처음부터 모든 퀘스트가 완료되어 있었다면 추가 보상 X
        boolean alreadyCompleted = isQuestFullyCompleted(quest);

        // 퀘스트 완료 처리 및 보상 지급
        if (!grantReward(member, quest, request)) return new StatusResponse(200, "퀘스트가 성공적으로 완료되었습니다.");

        // 퀘스트가 이번에 처음 완전히 완료되었다면 추가 보상 지급
        if (!alreadyCompleted && isQuestFullyCompleted(quest)) {
            memberRepository.updateShell(member.getId(), 25);
            shellService.saveShellLog(member, 25, Content.MISSION);
            return new StatusResponse(204, "추가 보상이 지급되었습니다.");
        }

        memberRepository.updateShell(member.getId(), 10);
        shellService.saveShellLog(member, 10, Content.MISSION);
        return new StatusResponse(200, "퀘스트가 성공적으로 완료되었습니다.");
    }

    /**
     * 매일 자정에 퀘스트 상태 초기화
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetQuestStatus() {
        List<Quest> quests = questRepository.findAll();

        for (Quest quest : quests) {
            quest.setDiaryStatus(false);
            quest.setQuizStatus(false);
            quest.setWordStatus(false);
        }
    }

    /**
     * 퀘스트 완료 처리 및 보상 지급
     */
    private boolean grantReward(Member member, Quest quest, UpdateQuestRequest request) {
        switch (request.getQuestType()) {
            case DIARY:
                if (quest.isDiaryStatus()) return false;
                quest.setDiaryStatus(true);
                break;
            case QUIZ:
                quest.setQuizStatus(true);
                break;
            case WORD:
                quest.setWordStatus(true);
                break;
            default:
                throw new IllegalArgumentException("존재하지 않는 퀘스트 타입입니다.");
        }
        return true;
    }

    /**
     * 퀘스트가 모두 완료되었는지 확인
     */
    private boolean isQuestFullyCompleted(Quest quest) {
        return quest.isDiaryStatus() && quest.isQuizStatus() && quest.isWordStatus();
    }
}
