package com.ssafy.daily.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.repository.DiaryRepository;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;
    private final LearnedWordRepository learnedWordRepository;
    private final DiaryRepository diaryRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String getFileExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if ("image/jpeg".equals(contentType)) {
            return ".jpg";
        } else if ("image/png".equals(contentType)) {
            return ".png";
        } else if ("video/mp4".equals(contentType)){
            return ".mp4";
        } else if ("video/webm".equals(contentType)){
            return ".webm";
        }else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식: " + contentType);
        }
    }

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String uniqueID = UUID.randomUUID().toString();
        String fileExtension = getFileExtension(multipartFile);
        String originalFilename = uniqueID + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public String saveFile(File file, String contentType) throws IOException {
        String uniqueID = UUID.randomUUID().toString();
        String fileExtension = getFileExtension(contentType);
        String originalFilename = uniqueID + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());
        metadata.setContentType(contentType);

        amazonS3.putObject(bucket, originalFilename, new FileInputStream(file), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }
    private String getFileExtension(String contentType) {
        if ("image/jpeg".equals(contentType)) {
            return ".jpg";
        } else if ("image/png".equals(contentType)) {
            return ".png";
        } else if ("video/mp4".equals(contentType)){
            return ".mp4";
        } else if ("video/webm".equals(contentType)){
            return ".webm";
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식: " + contentType);
        }
    }

    public String getWordDownloadUrl(Long learnedWordId) {
        LearnedWord learnedWord = learnedWordRepository.findById(learnedWordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학습한 단어 id가 없습니다: " + learnedWordId));

        return learnedWord.getImg();
    }

    public Map<String, String> getDiaryDownloadUrl(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 다이어리 id가 없습니다: " + diaryId));

        Map<String, String> urls = new HashMap<>();
        urls.put("writeImg", diary.getWriteImg());
        urls.put("drawImg", diary.getDrawImg());

        return urls;
    }

    public void deleteImage(String originalFilename) {
        amazonS3.deleteObject(bucket, originalFilename);
    }

    public String getFileNameFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            String[] segments = path.split("/");

            return segments[segments.length - 1];
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
