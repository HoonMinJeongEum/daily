package com.ssafy.daily.quiz.controller;

import com.ssafy.daily.quiz.dto.CheckWordRequest;
import com.ssafy.daily.quiz.dto.SetWordRequest;
import com.ssafy.daily.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizService quizService;

    // 토큰 생성
    @GetMapping( "/token")
    public ResponseEntity<?> createToken() {
        return ResponseEntity.ok(quizService.createToken());
    }
    
    // 웹훅
    @PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
    public ResponseEntity<String> receiveWebhook(@RequestHeader("Authorization") String authHeader, @RequestBody String body) {
        try {
            quizService.receiveWebhook(authHeader, body);
        } catch (Exception e) {
            System.err.println("Error validating webhook event: " + e.getMessage());
        }
        return ResponseEntity.ok("Webhook received");
    }
    
    // 단어 추천
    @GetMapping("/word/recommend")
    public ResponseEntity<?> recommendWord() {
        return ResponseEntity.ok(quizService.recommendWord());
    }
    
    // 단어 설정
    @PatchMapping("/word/set")
    public ResponseEntity<?> setWord(@RequestBody SetWordRequest request) {
        quizService.setWord(request);
        return ResponseEntity.ok("단어가 정상적으로 설정되었습니다.");
    }

    // 단어 정답 확인
    @PostMapping("/word/check")
    public ResponseEntity<?> checkWord(@RequestBody CheckWordRequest request) {
        boolean check = quizService.checkWord(request);
        return ResponseEntity.ok(check);
    }
}
