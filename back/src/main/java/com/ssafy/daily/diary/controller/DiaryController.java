package com.ssafy.daily.diary.controller;

import com.ssafy.daily.diary.dto.WriteCommentRequest;
import com.ssafy.daily.diary.service.DiaryService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<?> writeDiary(@AuthenticationPrincipal CustomUserDetails userDetails
//            , @RequestParam("drawFile") MultipartFile drawFile
//            , @RequestParam("writeFile") MultipartFile writeFile
    ){
        diaryService.writeDiary(userDetails, null, null);
        return ResponseEntity.ok("그림일기가 정상적으로 저장되었습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getDiaries(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(required = false) Integer memberId){
        return ResponseEntity.ok(diaryService.getDiaries(userDetails, memberId));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<?> getOneDiary(@PathVariable int diaryId){
        return ResponseEntity.ok(diaryService.getOneDiary(diaryId));
    }

    @PostMapping("/comment")
    public ResponseEntity<?> writeComment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody WriteCommentRequest request){
        diaryService.writeComment(userDetails, request);
        return ResponseEntity.ok("댓글 등록 성공");
    }


}
