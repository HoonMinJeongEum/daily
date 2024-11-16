package com.ssafy.daily.quiz.service;

import com.ssafy.daily.alarm.entity.Alarm;
import com.ssafy.daily.alarm.repository.AlarmRepository;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.quiz.dto.*;
import com.ssafy.daily.quiz.entity.Quiz;
import com.ssafy.daily.quiz.repository.QuizRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.entity.Word;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import com.ssafy.daily.word.repository.WordRepository;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class QuizService {
    private final WordRepository wordRepository;
    private final LearnedWordRepository learnedWordRepository;
    private final QuizRepository quizRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

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
    public SessionResponse initializeSession(CustomUserDetails userDetails, SessionRequest request) throws Exception {

        // 세션 아이디 생성
        Member member = userDetails.getMember();
        String username = userDetails.getUsername();
        String childName = userDetails.getMember().getName();
        String customSessionId = userDetails.getMember().getId() + username;
        Quiz quiz = quizRepository.findByMemberId(userDetails.getMember().getId());

        // 세션 생성
        Map<String,Object> map = new HashMap<>();
        map.put("customSessionId", customSessionId);
        SessionProperties properties = SessionProperties.fromJson(map).build();
        Session session = openvidu.createSession(properties);
        String sessionId = session.getSessionId();
        
        if (quiz == null) {
            Quiz newQuiz = Quiz.builder()
                    .sessionId(sessionId)
                    .member(member)
                    .build();
            quizRepository.save(newQuiz);
        }
        else {
            quiz.updateSessionId(sessionId);
            quiz.updateEndAt(null);
            quizRepository.save(quiz);
        }

        // 알림
        alarmService.sendNotification(childName, sessionId, userDetails.getFamily().getId(), Role.PARENT, "그림 퀴즈", childName + " - 그림 퀴즈를 요청 했어요");
        return new SessionResponse(sessionId);
    }

    // 토큰 생성
    public TokenResponse createConnection(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        }
        Map<String,Object> map = new HashMap<>();
        ConnectionProperties properties = ConnectionProperties.fromJson(map).build();
        Connection connection = session.createConnection(properties);
        return new TokenResponse(connection.getToken());
    }

    // 단어 추천
    public List<RecommendWordResponse> recommendWord(CustomUserDetails userDetails) {
        int memberId = userDetails.getMember().getId();
        List<LearnedWord> learnedWords = learnedWordRepository.findByMemberId(memberId);
        List<RecommendWordResponse> recommendedWords = new ArrayList<>();

        // 학습한 단어에서 랜덤으로 9개 추출
        if (learnedWords.size() >= 9) {
            Collections.shuffle(learnedWords);
            for (int i = 0; i < 9; i++) {
                LearnedWord learnedWord = learnedWords.get(i);
                recommendedWords.add(new RecommendWordResponse(learnedWord.getWord().getId(), learnedWord.getWord().getWord()));
            }
        }
        else {  // 만약 9개가 되지 않으면 Word에서 랜덤으로 9개 추출
            List<Word> allWords = wordRepository.findAll();
            Collections.shuffle(allWords);
            for (int i = 0; i < 9; i++) {
                Word word = allWords.get(i);
                recommendedWords.add(new RecommendWordResponse(word.getId(), word.getWord()));
            }
        }
        return recommendedWords;
    }

    // 세션 체크
    public CheckSessionResponse checkSession(CustomUserDetails userDetails, CheckSessionRequest request) {
        String username = userDetails.getUsername();
        String childName = request.getChildName();
        Member member = memberRepository.findByFamilyIdAndName(userDetails.getFamily().getId(), childName);
        String customSessionId = member.getId() + username;

        Quiz quiz = quizRepository.findBySessionId(customSessionId);

        if(quiz == null || quiz.getEndAt() != null) {
            return new CheckSessionResponse(null);
        }
        else {
            return new CheckSessionResponse(customSessionId);
        }
    }

    // 세션 종료
    public void endSession(CustomUserDetails userDetails) {
        // 알림 완료 처리
        String username = userDetails.getUsername();
        String customSessionId = userDetails.getMember().getId() + username;
        confirmAlarmsByTitleAndTitleId("그림 퀴즈", customSessionId);

        // 세션 종료
        Quiz quiz = quizRepository.findBySessionId(customSessionId);
        quiz.updateEndAt(LocalDateTime.now());
        quizRepository.save(quiz);
    }

    // 알림 확인 처리
    public void confirmAlarmsByTitleAndTitleId(String title, String titleId) {
        List<Alarm> alarms = alarmRepository.findByTitleAndTitleIdAndConfirmedAtIsNull(title, titleId);

        for (Alarm alarm : alarms) {
            alarm.confirm();
        }
        alarmRepository.saveAll(alarms);
    }
}
