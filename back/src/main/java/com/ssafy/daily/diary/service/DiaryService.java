package com.ssafy.daily.diary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.diary.dto.*;
import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.entity.DiaryComment;
import com.ssafy.daily.diary.repository.DiaryCommentRepository;
import com.ssafy.daily.diary.repository.DiaryRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Value(("${clova.ocr.apiUrl}"))
    private String apiUrl;

    @Value("${clova.ocr.secretKey}")
    private String secretKey;

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

    public void writeDiary(CustomUserDetails userDetails, MultipartFile drawFile, MultipartFile writeFile) {
        int memberId = userDetails.getMemberId();
        LocalDate today = LocalDate.now();
        boolean diaryExists = diaryRepository.findByMemberIdAndDate(memberId, today).isPresent();

        if (diaryExists) {
            throw new IllegalStateException("오늘 날짜의 일기는 이미 존재합니다.");
        }

        if (drawFile == null || drawFile.isEmpty()) {
            throw new IllegalArgumentException("그림 파일이 유효하지 않습니다.");
        }
        if (writeFile == null || writeFile.isEmpty()) {
            throw new IllegalArgumentException("일기 파일이 유효하지 않습니다.");
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

        List<FieldDto> fields = processOcr(writeImgUrl);

        String sound = generateBgm(fields);

        // DB에 저장
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));

        Diary diary = Diary.builder()
                .content(sound)     // 임시로 sound 데이터 저장
                .drawImg(drawImgUrl)
                .writeImg(writeImgUrl)
                .sound(sound)
                .member(member)
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
        String body = "업로드";
        try {
            alarmService.sendNotification(name, titleId, toId, role, title, body);
        } catch (Exception e) {
            throw new RuntimeException("그림일기 작성 알림 전송에 실패");
        }
    }

    public List<FieldDto> processOcr(String imgUrl) {
        Map<String, Object> requestBody = createRequestBody(imgUrl);
        ResponseEntity<String> response = callOcrApi(requestBody);

        // JSON 응답을 OcrResponse 객체로 변환
        OcrResponse ocrResponse = mapOcrResponse(response.getBody());

        // images 배열의 첫 번째 요소에서 fields 배열 가져오기
        return ocrResponse.getImages().get(0).getFields();
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
    private String generateBgm(List<FieldDto> fields) {
        // BGM 생성 로직 (필요한 처리를 수행)
        StringBuilder bgmText = new StringBuilder();
        for (FieldDto field : fields) {
            bgmText.append(field.getInferText()).append(" ");
        }
        // BGM 생성 로직 구현

        return bgmText.toString().trim(); // 예시로 텍스트만 반환
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
