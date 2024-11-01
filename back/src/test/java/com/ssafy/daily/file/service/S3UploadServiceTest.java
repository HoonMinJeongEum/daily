package com.ssafy.daily.file.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("secret") // application-secret.yml 설정 사용
public class S3UploadServiceTest {

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private AmazonS3 amazonS3;

    private final String testFileName = "cat-image.jpg";

    @Test
    public void testUploadAndDownload() throws IOException {
        // 파일 업로드 준비
        File catImageFile = new File("src/test/resources/cat-image.jpg");
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                testFileName,
                "image/jpeg",
                new FileInputStream(catImageFile)
        );

        // 파일 업로드
        String fileUrl = s3UploadService.saveFile(mockFile);
        System.out.println("Uploaded file URL: " + fileUrl);

        // 파일이 S3에 성공적으로 업로드되었는지 확인
        boolean doesExist = amazonS3.doesObjectExist("hoonminjunghmm-daily", testFileName);
        System.out.println("S3 object exists after upload: " + doesExist);
        assertThat(doesExist).isTrue();

        // S3에서 파일 다운로드 URL 확인
        String downloadUrl = s3UploadService.getDownloadUrl(testFileName).get("url");
        System.out.println("Download URL: " + downloadUrl);
        assertThat(downloadUrl).isNotEmpty();
    }

    @Test
    public void testDeleteImage() {
        // 파일 삭제
        s3UploadService.deleteImage(testFileName);
        System.out.println("File deleted: " + testFileName);

        // 파일이 S3에서 삭제되었는지 확인
        boolean doesExistAfterDelete = amazonS3.doesObjectExist("hoonminjunghmm-daily", testFileName);
        System.out.println("S3 object exists after delete: " + doesExistAfterDelete);
        assertThat(doesExistAfterDelete).isFalse();
    }
}
