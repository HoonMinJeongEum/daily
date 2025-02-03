package com.ssafy.daily.reward.controller;

import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.reward.dto.UpdateQuestRequest;
import com.ssafy.daily.reward.service.QuestService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quest")
public class QuestController {
    private final QuestService questService;

    /**
     * 퀘스트 완료 API
     *
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 퀘스트 완료 요청 DTO
     * @return 퀘스트 완료 상태 응답
     */
    @PatchMapping
    public ResponseEntity<StatusResponse> updateQuest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateQuestRequest request
    ) {
        StatusResponse response = questService.updateQuest(userDetails, request);
        return ResponseEntity.ok(response);
    }
}
