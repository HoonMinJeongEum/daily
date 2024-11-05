package com.ssafy.daily.word.controller;

import com.ssafy.daily.user.entity.Member;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/word/learned")
@RequiredArgsConstructor
public class LearnedWordController {

    private final WordService wordService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<LearnedWordResponse>> getOwnLearnedWords(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();
        List<LearnedWordResponse> learnedWords = wordService.getLearnedWordsByMember(memberId);

        if (learnedWords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(learnedWords);
    }

    @GetMapping("/{childId}")
    public ResponseEntity<List<LearnedWordResponse>> getChildLearnedWords(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int childId) {
        Optional<Member> optionalMember = memberRepository.findById(childId);

        if (optionalMember.isPresent()) {
            Member child = optionalMember.get();
            int childFamilyId = child.getFamily().getId();
            int parentFamilyId = userDetails.getFamilyId();
            int parentId = userDetails.getMemberId();

            if (parentFamilyId == childFamilyId && parentId == 0) {
                List<LearnedWordResponse> learnedWords = wordService.getLearnedWordsByMember(childId);

                if (learnedWords.isEmpty()) {
                    return ResponseEntity.noContent().build();
                }

                return ResponseEntity.ok(learnedWords);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
