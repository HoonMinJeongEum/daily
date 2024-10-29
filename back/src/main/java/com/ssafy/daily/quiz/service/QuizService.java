package com.ssafy.daily.quiz.service;

import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.quiz.dto.CheckWordRequest;
import com.ssafy.daily.quiz.dto.RecommendWordResponse;
import com.ssafy.daily.quiz.dto.SetWordRequest;
import com.ssafy.daily.quiz.entity.Quiz;
import com.ssafy.daily.quiz.repository.QuizRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.entity.Word;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import com.ssafy.daily.word.repository.WordRepository;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class QuizService {
    private final WordRepository wordRepository;
    private final LearnedWordRepository learnedWordRepository;
    private final QuizRepository quizRepository;
    private final AlarmService alarmService;

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    // 토큰 생성
    public Map<String, String> createToken(CustomUserDetails userDetails) throws Exception {
        int familyId = userDetails.getFamily().getId();
        String roomName = "quizRoom" + familyId;
        String participantName = (userDetails.getMember() != null)
                ? userDetails.getMember().getName()
                : userDetails.getFamily().getUsername();

        AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);

        token.setName(participantName);
        token.setIdentity(participantName);
        token.addGrants(new RoomJoin(true), new RoomName(roomName));
        if(userDetails.getMember() != null) {
            alarmService.sendNotification(participantName, 0, familyId, Role.PARENT, "그림 퀴즈", "요청");
        }

        return Map.of("token", token.toJwt());
    }

    // 웹 훅
    public void receiveWebhook(String authHeader, String body) {
        WebhookReceiver webhookReceiver = new WebhookReceiver(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        LivekitWebhook.WebhookEvent event = webhookReceiver.receive(body, authHeader);
        System.out.println("LiveKit Webhook: " + event.toString());
    }
    
    // 단어 추천
    public List<RecommendWordResponse> recommendWord(CustomUserDetails userDetails) {
        int memberId = userDetails.getMember().getId();
        List<LearnedWord> learnedWords = learnedWordRepository.findByMemberId(memberId);
        List<RecommendWordResponse> recommendedWords = new ArrayList<>();

        // 학습한 단어에서 랜덤으로 9개 추출
        if (learnedWords.size() >= 9) {
            Collections.shuffle(learnedWords); // 랜덤으로 섞기
            for (int i = 0; i < 9; i++) {
                LearnedWord learnedWord = learnedWords.get(i);
                recommendedWords.add(new RecommendWordResponse(learnedWord.getWord().getId(), learnedWord.getWord().getWord()));
            }
        }
        else {  // 만약 9개가 되지 않으면 Word에서 랜덤으로 9개 추출
            List<Word> allWords = wordRepository.findAll();
            Collections.shuffle(allWords); // 랜덤으로 섞기
            for (int i = 0; i < 9; i++) {
                Word word = allWords.get(i);
                recommendedWords.add(new RecommendWordResponse(word.getId(), word.getWord()));
            }
        }
        return recommendedWords;
    }

    // 단어 설정
    public void setWord(CustomUserDetails userDetails, SetWordRequest request) {
        int familyId = userDetails.getFamily().getId();
        Quiz quiz = quizRepository.findByFamilyId(familyId);
        quiz.updateWord(request.getWord());
        quizRepository.save(quiz);
    }
    
    // 단어 정답 확인
    public boolean checkWord(CustomUserDetails userDetails, CheckWordRequest request) {
        int familyId = userDetails.getFamily().getId();
        Quiz quiz = quizRepository.findByFamilyId(familyId);
        return request.getWord().equals(quiz.getWord());
    }
}
