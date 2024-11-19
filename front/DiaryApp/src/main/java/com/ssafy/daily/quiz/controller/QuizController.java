package com.ssafy.daily.quiz.controller;

import com.ssafy.daily.quiz.dto.*;
import com.ssafy.daily.quiz.service.QuizService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    // 세션 아이디 생성
    @PostMapping("/sessions")
    public ResponseEntity<?> initializeSession(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SessionRequest request)
            throws Exception {
        return ResponseEntity.ok(quizService.initializeSession(userDetails, request));
    }

    // 토큰 생성
    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<?> createConnection(@PathVariable("sessionId") String sessionId
                                              )
            throws OpenViduJavaClientException, OpenViduHttpException {
        return ResponseEntity.ok(quizService.createConnection(sessionId));
    }

    // 세션 아이디 체크
    @PostMapping("/sessions/check")
    public ResponseEntity<?> checkSession(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CheckSessionRequest request) {
        return ResponseEntity.ok(quizService.checkSession(userDetails, request));
    }

    // 세션 종료
    @PostMapping("/sessions/end")
    public ResponseEntity<?> endSession(@AuthenticationPrincipal CustomUserDetails userDetails) {
        quizService.endSession(userDetails);
        return ResponseEntity.ok().build();
    }

    // 단어 추천
    @GetMapping("/word/recommend")
    public ResponseEntity<?> recommendWord(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(quizService.recommendWord(userDetails));
    }
}
