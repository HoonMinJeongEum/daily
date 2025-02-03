package com.ssafy.daily.quiz.controller;

import com.ssafy.daily.quiz.dto.*;
import com.ssafy.daily.quiz.service.QuizService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    /**
     * 세션 아이디 생성 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param request 세션 생성 요청 데이터
     * @return 생성된 세션 정보
     */
    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> initializeSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SessionRequest request
    ) throws Exception {
        return ResponseEntity.ok(quizService.initializeSession(userDetails, request));
    }

    /**
     * 세션 연결 토큰 생성 API
     *
     * @param sessionId 세션 ID
     * @return 생성된 연결 토큰 정보
     */
    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<TokenResponse> createConnection(
            @PathVariable("sessionId") String sessionId
    ) throws OpenViduJavaClientException, OpenViduHttpException {
        return ResponseEntity.ok(quizService.createConnection(sessionId));
    }

    /**
     * 세션 유효성 확인 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param request 세션 체크 요청 데이터
     * @return 세션 유효성 확인 결과
     */
    @PostMapping("/sessions/check")
    public ResponseEntity<CheckSessionResponse> checkSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CheckSessionRequest request
    ) {
        return ResponseEntity.ok(quizService.checkSession(userDetails, request));
    }

    /**
     * 세션 종료 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     */
    @PostMapping("/sessions/end")
    public ResponseEntity<Void> endSession(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        quizService.endSession(userDetails);
        return ResponseEntity.ok().build();
    }

    /**
     * 단어 추천 API
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @return 추천된 단어 리스트
     */
    @GetMapping("/word/recommend")
    public ResponseEntity<List<RecommendWordResponse>> recommendWord(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(quizService.recommendWord(userDetails));
    }
}
