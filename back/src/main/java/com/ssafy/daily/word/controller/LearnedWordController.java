// src/main/java/com/ssafy/daily/word/controller/LearnedWordController.java
package com.ssafy.daily.word.controller;

import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ssafy.daily.user.dto.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/word/learned")
@RequiredArgsConstructor
public class LearnedWordController {

    private final WordService wordService;

    @GetMapping
    public ResponseEntity<List<LearnedWordResponse>> getLearnedWordsByMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();
        List<LearnedWordResponse> learnedWords = wordService.getLearnedWordsByMember(memberId);

        if (learnedWords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(learnedWords);
    }

}
