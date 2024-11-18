package com.ssafy.daily.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.diary.dto.*;
import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.entity.DiaryComment;
import com.ssafy.daily.diary.repository.DiaryCommentRepository;
import com.ssafy.daily.diary.repository.DiaryRepository;
import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.S3UploadException;
import com.ssafy.daily.file.service.S3UploadService;
import com.ssafy.daily.reward.dto.CouponResponse;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.RequestHeaderFieldsTooLargeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final AlarmService alarmService;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final ObjectMapper objectMapper;
    private final S3UploadService s3UploadService;
    private final RestTemplate restTemplate;
    private static final int DEFAULT_TOKENS = 500;

    @Value(("${clova.ocr.apiUrl}"))
    private String apiUrl;

    @Value("${clova.ocr.secretKey}")
    private String secretKey;

    String fastApiUrl = "http://175.209.203.185:9290/generate-bgm/";

    public List<MonthlyDiaryResponse> getDiaries(CustomUserDetails userDetails, Integer memberId,
                                          int year, int month) {
        if (memberId == null) {
            memberId = userDetails.getMemberId();
        }
        List<Diary> list = diaryRepository.findByMemberIdAndYearAndMonth(memberId, year, month);

        return list.stream()
                .map(MonthlyDiaryResponse::new)
                .collect(Collectors.toList());
    }

    public StatusResponse writeDiary(CustomUserDetails userDetails, MultipartFile drawFile, MultipartFile writeFile, MultipartFile videoFile) {
        int memberId = userDetails.getMemberId();
        LocalDate today = LocalDate.now();
        boolean diaryExists = diaryRepository.findByMemberIdAndDate(memberId, today).isPresent();

        if (diaryExists) {
            throw new AlreadyOwnedException("오늘 날짜의 일기는 이미 존재합니다.");
        }

        if (drawFile == null || drawFile.isEmpty()) {
            throw new IllegalArgumentException("그림 파일이 유효하지 않습니다.");
        }
        if (writeFile == null || writeFile.isEmpty()) {
            throw new IllegalArgumentException("일기 파일이 유효하지 않습니다.");
        }
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("동영상 파일이 유효하지 않습니다.");
        }

        String drawImgUrl = null;
        try {
            drawImgUrl = s3UploadService.saveFile(drawFile);
        } catch (IOException e) {
            throw new S3UploadException("S3 그림 이미지 업로드 실패");
        }
        String writeImgUrl = null;
        try {
            writeImgUrl = s3UploadService.saveFile(writeFile);
        } catch (IOException e) {
            throw new S3UploadException("S3 일기 이미지 업로드 실패");
        }
        String videoUrl;
        File mp4TempFile = null;
        File webmTempFile = null;
        try {
            // MultipartFile을 임시 mp4 파일로 저장
            mp4TempFile = File.createTempFile("temp-video", ".mp4");
            videoFile.transferTo(mp4TempFile);

            // 임시 webm 파일 생성
            webmTempFile = File.createTempFile("temp-video", ".webm");

            System.out.println("mp4TempFile: " + mp4TempFile.getAbsolutePath());
            System.out.println("webmTempFile: " + webmTempFile.getAbsolutePath());


            // FFmpeg로 mp4를 webm으로 변환
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", mp4TempFile.getAbsolutePath(),
                    "-c:v", "libvpx",
                    "-c:a", "libvorbis",
                    webmTempFile.getAbsolutePath()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("동영상 변환 실패");
                throw new S3UploadException("동영상 변환 실패");
            }

            if (!webmTempFile.exists()) {
                System.out.println("변환된 파일이 존재하지 않습니다.");
                throw new S3UploadException("변환된 파일이 존재하지 않습니다.");
            }

            // 변환된 webm 파일을 S3에 업로드
            videoUrl = s3UploadService.saveFile(webmTempFile, "video/webm");

        } catch (IOException | InterruptedException e) {
            System.out.println("예외 발생: " + e.getMessage());
            throw new S3UploadException("S3 동영상 업로드 실패: " + e.getMessage());
        } finally {
            // 임시 파일 삭제
            if (mp4TempFile != null && mp4TempFile.exists()) {
                mp4TempFile.delete();
            }
            if (webmTempFile != null && webmTempFile.exists()) {
                webmTempFile.delete();
            }
        }

        String content = processOcr(writeImgUrl);

        String sound = generateBgm(content);

        Member member = memberRepository.findById(memberId)
                .orElseThrow();

        Diary diary = Diary.builder()
                .content(content)
                .drawImg(drawImgUrl)
                .writeImg(writeImgUrl)
                .sound(sound)
                .member(member)
                .video(videoUrl)
                .build();
        diaryRepository.save(diary);

        // 알림 전송
       /*
        name : 보내는 사람의 이름
        titleId : 수락이나 확인 누를 시 이동할 페이지에 필요한 id (그림 일기의 id or 그림 퀴즈 sessionId)
        toId : 받는 사람의 Id
        role : 받는 사람의 role(PARENT or CHILD)
        title : 알림 제목 (그림 일기 or 그림 퀴즈)
        body : 알림 내용 ex) 그림 퀴즈 요청
        */
        String name = userDetails.getMember().getName();
        String titleId = String.valueOf(diary.getId());
        int toId = userDetails.getFamilyId();
        Role role = Role.PARENT;
        String title = "그림 일기";
        String body = name + " - 그림 일기를 업로드 했어요";
        try {
            alarmService.sendNotification(name, titleId, toId, role, title, body);
        } catch (Exception e) {
            throw new S3UploadException("그림일기 작성 알림 전송에 실패");
        }
        return new StatusResponse(200, "그림일기가 정상적으로 저장되었습니다.");
    }

    public String processOcr(String imgUrl) {
        Map<String, Object> requestBody = createRequestBody(imgUrl);
        ResponseEntity<String> response = callOcrApi(requestBody);

        // JSON 응답을 OcrResponse 객체로 변환
        OcrResponse ocrResponse = mapOcrResponse(response.getBody());

        // images 배열의 첫 번째 요소에서 fields 배열 가져오기
        List<FieldDto> fields = ocrResponse.getImages().get(0).getFields();

        StringBuilder ocrText = new StringBuilder();
        for (FieldDto field : fields) {
            ocrText.append(field.getInferText()).append(" ");
        }

        return ocrText.toString().trim();
    }

    private Map<String, Object> createRequestBody(String imgUrl) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("images", new Object[]{new HashMap<String, Object>() {{
            put("format", "jpg");
            put("name", "medium");
            put("data", null);
            put("url", imgUrl);
        }}});
        requestBody.put("lang", "ko");
        requestBody.put("requestId", UUID.randomUUID().toString());
        requestBody.put("resultType", "string");
        requestBody.put("timestamp", System.currentTimeMillis());
        requestBody.put("version", "V1");
        return requestBody;
    }

    private ResponseEntity<String> callOcrApi(Map<String, Object> requestBody) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", secretKey);

        // Clova OCR API 호출
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return new RestTemplate().exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
    }

    private OcrResponse mapOcrResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, OcrResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse OCR response", e);
        }
    }
    private String generateBgm(String content) {

        BgmRequestDto request = new BgmRequestDto(content, DEFAULT_TOKENS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BgmRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, entity, String.class);
        String responseBody = response.getBody();
        try {
            // JSON 응답을 Map으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return (String) responseMap.get("s3_url");
        } catch (Exception e) {
            throw new RuntimeException("응답에서 s3_url을 추출할 수 없습니다.", e);
        }
    }

    public DiaryResponse getOneDiary(int diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 그림일기를 찾을 수 없습니다.", 1));
        List<CommentResponse> comments = diaryCommentRepository.findByDiaryId(diary.getId())
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());

        return new DiaryResponse(
                diary,
                comments  // CommentResponse 리스트로 설정
        );
    }

    public void writeComment(CustomUserDetails userDetails, WriteCommentRequest request) {
        int diaryId = request.getDiaryId();
        String comment = request.getComment();
        Family family = familyRepository.findById(userDetails.getFamilyId())
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 가족 계정을 찾을 수 없습니다.", 1));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 그림일기를 찾을 수 없습니다.", 1));

        DiaryComment diaryComment = DiaryComment.builder()
                .family(family)
                .diary(diary)
                .comment(comment)
                .build();
        diaryCommentRepository.save(diaryComment);

        String name = "부모님";
        String titleId = String.valueOf(diaryId);
        int toId = diary.getMember().getId();
        Role role = Role.CHILD;
        String title = "그림 일기";
        String body = "그림 일기에 답글이 왔어요!";
        try {
            alarmService.sendNotification(name, titleId, toId, role, title, body);
        } catch (Exception e) {
            throw new RuntimeException("그림일기 작성 알림 전송에 실패");
        }
    }

}
