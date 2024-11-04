//package com.ssafy.daily.file.service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//public class S3UploadServiceTest {
//
//    @Autowired
//    private S3UploadService s3UploadService;
//
//    @Autowired
//    private AmazonS3 amazonS3;
//
//    private String drawFileUrl = "";
//    private String writeFileUrl = "https://sjsbucket.s3.ap-northeast-2.amazonaws.com/46039185-887c-494f-8398-3b92d100e361.jpg";
//
//    @Test
//    public void testUpload() throws IOException {
//        // 파일 업로드 준비
//        File catImageFile = new File("src/test/resources/cat-image.jpg");
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "file",
//                "cat-image.jpg",
//                "image/jpeg",
//                new FileInputStream(catImageFile)
//        );
//
//        // 파일 업로드 1
//        writeFileUrl = s3UploadService.saveFile(mockFile);
//        System.out.println("Uploaded file URL: " + writeFileUrl);
//        assertThat(writeFileUrl).isNotEmpty();
//
////        파일 업로드 2 (그림 일기 저장시 이름 안 겹치나 보기 위해 두 번 테스트)
//        drawFileUrl = s3UploadService.saveFile(mockFile);
//        System.out.println("Uploaded file URL: " + drawFileUrl);
//        assertThat(drawFileUrl).isNotEmpty();
//
//        Map<String, String> urls = new HashMap<>();
//        urls.put("writeImg", writeFileUrl);
//        urls.put("drawImg", drawFileUrl);
//        System.out.println(urls);
//    }
//
//    @Test
//    public void testDelete() {
//        // 파일 삭제
//        String fileName = s3UploadService.getFileNameFromUrl(writeFileUrl);
//        System.out.println("url is " + writeFileUrl);
//        System.out.println("file name is " + fileName);
//        s3UploadService.deleteImage(fileName);
//        System.out.println("File deleted: " + fileName);
//
//        // 파일이 S3에서 삭제되었는지 확인
//        boolean doesExistAfterDelete = amazonS3.doesObjectExist("sjsbucket", fileName);
//        System.out.println("S3 object exists after delete: " + doesExistAfterDelete);
//        assertThat(doesExistAfterDelete).isFalse();
//    }
//}
