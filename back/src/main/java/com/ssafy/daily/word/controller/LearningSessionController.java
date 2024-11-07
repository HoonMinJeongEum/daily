package com.ssafy.daily.word.controller;

import com.ssafy.daily.word.dto.LearningWordResponse;
import com.ssafy.daily.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ssafy.daily.user.dto.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/word/session")
@RequiredArgsConstructor
public class LearningSessionController {

    private final WordService wordService;

    @GetMapping
    public ResponseEntity<List<LearningWordResponse>> startLearningSession(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();

        List<LearningWordResponse> unlearnedWords = wordService.getUnlearnedWords(memberId);
        return ResponseEntity.ok(unlearnedWords);
    }

    @PostMapping(value = "/complete", consumes = {"multipart/form-data"})
    public ResponseEntity<String> completeLearningSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("ids") List<Integer> ids,
            @RequestPart("writeFile") List<MultipartFile> writeFile) {

        if (ids.size() != writeFile.size()) {
            return ResponseEntity.badRequest().body("ID 수와 파일 수가 일치하지 않습니다.");
        }

        int memberId = userDetails.getMemberId();
        wordService.markWordsAsLearned(memberId, ids, writeFile);

        return ResponseEntity.status(HttpStatus.CREATED).body("자식 사용자가 단어 학습을 완료하였습니다.");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> checkWordSimilarity(@RequestPart MultipartFile writeFile,
                                                 @RequestPart String word){
        return ResponseEntity.ok(wordService.checkSimilarity(word, writeFile));
    }
}