package com.ssafy.daily.storage;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

//    private final AmazonS3 amazonS3;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    public String upload(MultipartFile file, String filePath) {
//        try {
//            amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), null));
//            return amazonS3.getUrl(bucketName, filePath).toString();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload file to S3", e);
//        }
//    }
}
