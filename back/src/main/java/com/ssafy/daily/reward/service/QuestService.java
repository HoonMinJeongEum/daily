package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.quiz.entity.Quiz;
import com.ssafy.daily.quiz.repository.QuizRepository;
import com.ssafy.daily.quiz.service.QuizService;
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

@RequiredArgsConstructor
@Service
public class QuestService {
    private final QuestRepository questRepository;
    private final ShellService shellService;
    private final MemberRepository memberRepository;
    private final QuizRepository quizRepository;

    // 퀘스트 완료
    public StatusResponse updateQuest(CustomUserDetails userDetails, UpdateQuestRequest request) {
        int memberId = userDetails.getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("구성원을 찾을 수 없습니다."));
        Quest quest = questRepository.findByMemberId(memberId);
        boolean check = quest.isDiaryStatus() && quest.isWordStatus() && quest.isQuizStatus();

        switch (request.getQuestType()) {
            case DIARY:
                if (!quest.isDiaryStatus()) {
                    shellService.saveShellLog(member, 10, Content.MISSION);
                    quest.setDiaryStatus(true);
                }
                break;
            case QUIZ:
                shellService.saveShellLog(member, 10, Content.MISSION);
                quest.setQuizStatus(true);
                break;
            case WORD:
                shellService.saveShellLog(member, 10, Content.MISSION);
                quest.setWordStatus(true);
                break;
            default:
                throw new IllegalArgumentException("존재하지 않는 퀘스트 타입입니다.");
        }
        questRepository.save(quest);

        // 모든 퀘스트가 완료된 경우 추가 보상 지급
        if (!check && quest.isDiaryStatus() && quest.isQuizStatus() && quest.isWordStatus()) {
            shellService.saveShellLog(member, 15, Content.MISSION);
            return new StatusResponse(204, "추가 보상이 지급되었습니다.");
        }

        return new StatusResponse(200, "퀘스트가 성공적으로 완료되었습니다.");
    }

    // 매일 자정에 퀘스트 상태 초기화
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetQuestStatus() {
        List<Quest> quests = questRepository.findAll();

        for (Quest quest : quests) {
            quest.setDiaryStatus(false);
            quest.setQuizStatus(false);
            quest.setWordStatus(false);
        }
        questRepository.saveAll(quests);
    }
}
