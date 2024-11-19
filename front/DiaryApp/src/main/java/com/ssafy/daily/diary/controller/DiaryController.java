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
            , @RequestPart("drawFile") MultipartFile drawFile
            , @RequestPart("writeFile") MultipartFile writeFile
            ,@RequestPart("videoFile") MultipartFile videoFile){
        return ResponseEntity.ok(diaryService.writeDiary(userDetails, drawFile, writeFile, videoFile));
    }

    @GetMapping
    public ResponseEntity<?> getDiaries(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestParam(required = false) Integer memberId,
                                        @RequestParam int year, @RequestParam int month){
        return ResponseEntity.ok(diaryService.getDiaries(userDetails, memberId, year, month));
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
