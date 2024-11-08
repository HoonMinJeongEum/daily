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

        // 퀘스트 완료 상태 업데이트
        boolean isQuestUpdated = updateQuestStatus(userDetails, quest, request);

        if (isQuestUpdated) {
            // 기본 보상 지급
            shellService.saveShellLog(member, (byte) 10, Content.MISSION);

            // 모든 퀘스트가 완료된 경우 추가 보상 지급
            if (quest.isDiaryStatus() && quest.isQuizStatus() && quest.isWordStatus()) {
                shellService.saveShellLog(member, (byte) 15, Content.MISSION);
            }

            // 퀘스트 업데이트 저장
            questRepository.save(quest);
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

    // 퀘스트 상태 업데이트
    private boolean updateQuestStatus(CustomUserDetails userDetails, Quest quest, UpdateQuestRequest request) {
        switch (request.getQuestType()) {
            case DIARY:
                if (!quest.isDiaryStatus()) {
                    quest.setDiaryStatus(true);
                    return true;
                }
                break;
            case QUIZ:
                if (!quest.isQuizStatus()) {
                    quest.setQuizStatus(true);
                    return true;
                }
                break;
            case WORD:
                if (!quest.isWordStatus()) {
                    quest.setWordStatus(true);
                    return true;
                }
                break;
            default:
                throw new IllegalArgumentException("존재하지 않는 퀘스트 타입입니다.");
        }
        return false;
    }

}
