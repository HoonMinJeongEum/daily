package com.ssafy.daily.diary.service;

import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.diary.dto.CommentResponse;
import com.ssafy.daily.diary.dto.DiaryResponse;
import com.ssafy.daily.diary.dto.WriteCommentRequest;
import com.ssafy.daily.diary.dto.WriteDiaryRequest;
import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.entity.DiaryComment;
import com.ssafy.daily.diary.repository.DiaryCommentRepository;
import com.ssafy.daily.diary.repository.DiaryRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final AlarmService alarmService;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;

    public List<DiaryResponse> getDiaries(CustomUserDetails userDetails, Integer memberId) {
        // memberId가 null이면 JWT 토큰에서 가져옴
        if (memberId == null) {
            memberId = userDetails.getMemberId();
        }
        List<Diary> list = diaryRepository.findByMemberId(memberId);

        // DiaryResponse로 변환 후 반환
        return list.stream().map(diary -> {
            // 다이어리와 연결된 모든 DiaryComment 가져오기
            List<CommentResponse> comments = diaryCommentRepository.findByDiaryId(diary.getId())
                    .stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());

            return new DiaryResponse(
                    diary,
                    comments  // CommentResponse 리스트로 설정
            );
        }).collect(Collectors.toList());
    }

    public void writeDiary(CustomUserDetails userDetails, WriteDiaryRequest request) {
        String content = request.getContent();
        String img = request.getImg();
        String sound = request.getSound();

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("그림일기 내용을 작성해주세요.");
        }
        if (img == null || img.isEmpty()) {
            throw new IllegalArgumentException("그림일기 이미지가 유효하지 않습니다.");
        }
        if (sound == null || sound.isEmpty()) {
            throw new IllegalArgumentException("그림일기 BGM이 유효하지 않습니다.");
        }

        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));

        Diary diary = Diary.builder()
                .content(content)
                .img(img)
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
//        String name = userDetails.getMember().getName();
//        String titleId = String.valueOf(diary.getId());
//        int toId = userDetails.getFamilyId();
//        Role role = Role.PARENT;
//        String title = "그림 일기";
//        String body = "그림 일기 업로드";
//        try {
//            alarmService.sendNotification(name, titleId, toId, role, title, body);
//        } catch (Exception e) {
//            throw new RuntimeException("그림일기 작성 알림 전송에 실패");
//        }
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
    }
}
