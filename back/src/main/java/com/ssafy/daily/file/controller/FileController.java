package com.ssafy.daily.file.controller;

import com.ssafy.daily.file.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3UploadService s3UploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = s3UploadService.saveFile(file);
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/download-url")
    public ResponseEntity<Map<String, String>> downloadFile(@RequestParam("filename") String filename) {
        String fileUrl = s3UploadService.getDownloadUrl(filename).get("url");

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("filename") String filename) {
        s3UploadService.deleteImage(filename);
        return ResponseEntity.ok("File deleted successfully.");
    }
}
