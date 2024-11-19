package com.ssafy.daily.user.service;

import com.ssafy.daily.alarm.entity.FCMToken;
import com.ssafy.daily.alarm.repository.AlarmRepository;
import com.ssafy.daily.alarm.service.AlarmService;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.repository.DiaryCommentRepository;
import com.ssafy.daily.diary.repository.DiaryRepository;
import com.ssafy.daily.exception.*;
import com.ssafy.daily.file.service.S3UploadService;
import com.ssafy.daily.quiz.entity.Quiz;
import com.ssafy.daily.quiz.repository.QuizRepository;
import com.ssafy.daily.reward.entity.Quest;
import com.ssafy.daily.reward.repository.EarnedCouponRepository;
import com.ssafy.daily.reward.repository.EarnedStickerRepository;
import com.ssafy.daily.reward.repository.QuestRepository;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.reward.service.ShellService;
import com.ssafy.daily.user.dto.*;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.entity.Refresh;
import com.ssafy.daily.user.jwt.JWTUtil;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import com.ssafy.daily.user.repository.RefreshRepository;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final RefreshRepository refreshRepository;
    private final QuizRepository quizRepository;
    private final QuestRepository questRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final ShellService shellService;
    private final AuthenticationManager authenticationManager;
    private final S3UploadService s3UploadService;
    private final DiaryRepository diaryRepository;
    private final LearnedWordRepository learnedWordRepository;
    private final EarnedCouponRepository earnedCouponRepository;
    private final ShellRepository shellRepository;
    private final EarnedStickerRepository earnedStickerRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;

    public void checkExist(String username){
        Boolean isExist = familyRepository.existsByUsername(username);
        if (isExist){
            throw new UsernameAlreadyExistsException("이미 사용중인 아이디입니다.");
        }
    }

    public void join(JoinRequest request){
        String username = request.getUsername();
        String password = request.getPassword();

        checkExist(username);

        if (!username.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new IllegalArgumentException("아이디는 영어와 숫자를 포함한 4-20자로 설정해야 합니다.");
        }

        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$")) {
            throw new IllegalArgumentException("비밀번호는 영어, 숫자, 특수문자를 포함한 6-20자로 설정해야 합니다.");
        }

        Family family = Family.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        familyRepository.save(family);

    }

    public List<ProfilesResponse> getProfiles(CustomUserDetails userDetails) {
        int familyId = userDetails.getFamily().getId();
        List<Member> list = memberRepository.findByFamilyId(familyId);

        return list.stream()
                .map(member -> {
                    int shellCount = shellService.getUserShell(member.getId());
                    return new ProfilesResponse(member, shellCount);
                })
                .collect(Collectors.toList());
    }

    public void addProfile(CustomUserDetails userDetails, MultipartFile file, String memberName) {
        if (memberName.length() > 5 || memberName.isEmpty()){
            throw new IllegalArgumentException("이름은 5자 이내로 설정해야 합니다.");
        }

        int familyId = userDetails.getFamily().getId();
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 가족 계정을 찾을 수 없습니다.", 1));

        boolean isDuplicate = memberRepository.existsByFamilyIdAndName(familyId, memberName);
        if (isDuplicate) {
            throw new AlreadyOwnedException("이미 존재하는 이름입니다. 다른 이름을 사용하세요.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("프로필 이미지 파일이 필요합니다.");
        }

        String imageUrl = null;
        try {
            imageUrl = s3UploadService.saveFile(file);
        } catch (IOException e) {
            throw new S3UploadException("S3 프로필 이미지 업로드 실패");
        }

        Member member = Member.builder()
                .name(memberName)
                .family(family)
                .img(imageUrl)
                .build();
        memberRepository.save(member);

        Quest quest = Quest.builder()
                .member(member)
                .build();
        questRepository.save(quest);
    }

    public String choiceMember(CustomUserDetails userDetails, ChoiceMemberRequest request, HttpServletResponse response) {
        int memberId = request.getMemberId();
        int familyId = userDetails.getFamilyId();

        Member member = memberRepository.findByIdAndFamilyId(memberId, familyId)
                .orElseThrow(() -> new IllegalArgumentException("현재 계정에 해당 프로필이 존재하지 않습니다.: " + memberId));

        String newAccess = jwtUtil.createJwt("access", userDetails.getUsername(), "ROLE", userDetails.getFamilyId(), memberId, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userDetails.getUsername(), "ROLE", userDetails.getFamilyId(), memberId, 86400000L);

        addRefreshEntity(userDetails.getUsername(), newRefresh, 86400000L); // 기존의 refresh 토큰을 대체

        Cookie refreshCookie = createCookie("refresh", newRefresh);
        response.addCookie(refreshCookie);

        return newAccess;
    }
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.createRefresh(username, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }

    public MainProfileResponse getMainProfile(CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));
        Quest quest = questRepository.findByMemberId(memberId);
        if (quest == null) {
            throw new QuestNotFoundException("프로필에 해당하는 퀘스트를 찾을 수 없습니다.");
        }
        int shellCount = shellService.getUserShell(memberId);

        return new MainProfileResponse(member, quest, shellCount);
    }

    public void login(LoginRequest request, HttpServletResponse response){
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            );
            Authentication authentication = authenticationManager.authenticate(authToken);
            String username = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.iterator().next().getAuthority();
            int familyId = ((CustomUserDetails) authentication.getPrincipal()).getFamilyId();

            String accessToken = jwtUtil.createAccessJwt(username, role, familyId, 0, 600000L);
            String refreshToken = jwtUtil.createRefreshJwt(username, role, familyId, 0, 86400000L);

            addRefreshEntity(username, refreshToken, 86400000L);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.addCookie(createCookie("refresh", refreshToken));
        }  catch (AuthenticationException e) {
            throw new LoginFailedException("로그인 실패: 잘못된 아이디 또는 비밀번호입니다.");
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        if (refresh == null) {
            throw new InvalidRefreshTokenException("Refresh 토큰이 존재하지 않습니다.");
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new InvalidRefreshTokenException("만료된 토큰입니다.");
        }

        if (!jwtUtil.isRefreshToken(refresh)){
            throw new InvalidRefreshTokenException("유효하지 않은 토큰입니다.");
        }

        boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new InvalidRefreshTokenException("DB에 존재하지 않는 토큰입니다.");
        }

        refreshRepository.deleteByRefresh(refresh);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Transactional
    public void deleteMember(int memberId) {
        questRepository.deleteByMemberId(memberId);
        List<Diary> diaries = diaryRepository.findByMemberId(memberId);
        for (Diary diary : diaries) {
            diaryCommentRepository.deleteByDiaryId(diary.getId());
        }
        diaryRepository.deleteByMemberId(memberId);
        learnedWordRepository.deleteByMemberId(memberId);
        earnedCouponRepository.deleteByMemberId(memberId);
        shellRepository.deleteByMemberId(memberId);
        earnedStickerRepository.deleteByMemberId(memberId);
        quizRepository.deleteByMemberId(memberId);

        
        // 알림 삭제
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + memberId));
        FCMToken fcmToken = alarmService.getToken(member.getFamily().getId(), Role.PARENT);

        if (fcmToken != null) {
            alarmRepository.deleteByNameAndFcmToken_Id(member.getName(), fcmToken.getId());
        }
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void modifyMemberName(ModifyNameRequest request, CustomUserDetails userDetails) {
        String newName = request.getName();
        if (newName.length() > 20 || newName.isEmpty()){
            throw new IllegalArgumentException("이름은 20자 이내로 설정해야 합니다.");
        }

        int memberId = userDetails.getMemberId();

        FCMToken fcmToken = alarmService.getToken(userDetails.getFamilyId(), Role.PARENT);
        alarmRepository.updateAlarmNameByFcmToken(userDetails.getMember().getName(), newName, fcmToken.getId());
        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));

        orgMember.updateName(newName);
    }

    @Transactional
    public ModifyImgResponse modifyProfileImg(MultipartFile file, CustomUserDetails userDetails) {
        int memberId = userDetails.getMemberId();

        Member orgMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));

        String imageUrl = null;
        try {
            imageUrl = s3UploadService.saveFile(file);
        } catch (IOException e) {
            throw new S3UploadException("S3 프로필 이미지 업로드 실패");
        }

        orgMember.updateImg(imageUrl);
        return new ModifyImgResponse(imageUrl);
    }
}
