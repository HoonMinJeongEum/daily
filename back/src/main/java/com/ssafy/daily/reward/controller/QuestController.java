package com.ssafy.daily.reward.controller;

import com.ssafy.daily.reward.dto.UpdateQuestRequest;
import com.ssafy.daily.reward.service.QuestService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quest")
public class QuestController {
    private final QuestService questService;

    // 퀘스트 완료
    @PatchMapping
    public ResponseEntity<?> updateQuest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateQuestRequest request) {
        return ResponseEntity.ok(questService.updateQuest(userDetails, request));
    }
}
