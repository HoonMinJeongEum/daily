// src/main/java/com/ssafy/daily/word/controller/LearnedWordController.java
package com.ssafy.daily.word.controller;

import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.service.LearnedWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.ssafy.daily.user.dto.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
public class LearnedWordController {

    private final LearnedWordService learnedWordService;

    @GetMapping("/learned")
    public ResponseEntity<List<LearnedWordResponse>> getLearnedWordsByMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();
        List<LearnedWordResponse> learnedWords = learnedWordService.getLearnedWordsByMember(memberId);

        if (learnedWords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(learnedWords);
    }
}
