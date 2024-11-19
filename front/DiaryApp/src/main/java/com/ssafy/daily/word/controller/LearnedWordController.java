package com.ssafy.daily.word.controller;

import com.ssafy.daily.user.repository.MemberRepository;
import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ssafy.daily.user.dto.CustomUserDetails;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/word/learned")
@RequiredArgsConstructor
public class LearnedWordController {

    private final WordService wordService;

    @GetMapping
    public ResponseEntity<List<LearnedWordResponse>> getOwnLearnedWords(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LearnedWordResponse> learnedWords = wordService.getLearnedWordsByMember(userDetails.getMemberId());

        if (learnedWords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(learnedWords);
    }

    @GetMapping("/{childId}")
    public ResponseEntity<List<LearnedWordResponse>> getChildLearnedWords(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int childId) {
        List<LearnedWordResponse> learnedWords = wordService.getChildLearnedWordsWithParentCheck(userDetails, childId);

        if (learnedWords == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        if (learnedWords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(learnedWords);
    }

}
