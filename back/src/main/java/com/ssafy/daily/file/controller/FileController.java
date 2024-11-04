package com.ssafy.daily.file.controller;

import com.ssafy.daily.file.service.S3UploadService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3UploadService s3UploadService;

//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        try {
//            int memberId = userDetails.getMemberId();
//            String fileUrl = s3UploadService.saveFile(file, memberId);
//            return ResponseEntity.ok(fileUrl);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("파일 업로드 중 오류 발생: " + e.getMessage());
//        }
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            int memberId) {
        try {
            String fileUrl = s3UploadService.saveFile(file);
            return ResponseEntity.ok(fileUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    @GetMapping("/download/word")
    public ResponseEntity<Map<String, String>> downloadWordFile(Long learnedWordId) {
        String fileUrl = s3UploadService.getWordDownloadUrl(learnedWordId);

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/download/diary")
//    public ResponseEntity<Map<String, String>> downloadDiaryFile(@RequestParam("diaryId") int diaryId) {
//        Map<String, String> urls = s3UploadService.getDiaryDownloadUrl(diaryId);
//        return ResponseEntity.ok(urls);
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(
            @RequestParam("fileUrl") String fileUrl) {
        String fileName = s3UploadService.getFileNameFromUrl(fileUrl);
        s3UploadService.deleteImage(fileName);
        return ResponseEntity.ok("파일이 정상적으로 삭제되었습니다.");
    }
}
