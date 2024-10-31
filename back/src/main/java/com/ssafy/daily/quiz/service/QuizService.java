package com.ssafy.daily.quiz.service;

import com.ssafy.daily.alarm.service.AlarmService;
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
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.*;

@RequiredArgsConstructor
@Service
public class QuizService {
    private final WordRepository wordRepository;
    private final LearnedWordRepository learnedWordRepository;
    private final QuizRepository quizRepository;
    private final AlarmService alarmService;

    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    // 세션 아이디 생성
    public String initializeSession(Map<String, Object> params) throws OpenViduJavaClientException, OpenViduHttpException {
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openvidu.createSession(properties);
        return session.getSessionId();
    }

    // 토큰 생성
    public String createConnection(String sessionId, Map<String, Object> params) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return connection.getToken();
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
