package com.ssafy.daily.user.service;

import com.ssafy.daily.exception.QuestNotFoundException;
import com.ssafy.daily.exception.UsernameAlreadyExistsException;
import com.ssafy.daily.quiz.entity.Quiz;
import com.ssafy.daily.quiz.repository.QuizRepository;
import com.ssafy.daily.reward.entity.Quest;
import com.ssafy.daily.reward.repository.QuestRepository;
import com.ssafy.daily.reward.service.ShellService;
import com.ssafy.daily.user.dto.*;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.entity.Refresh;
import com.ssafy.daily.user.jwt.JWTUtil;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import com.ssafy.daily.user.repository.RefreshRepository;
import com.sun.tools.javac.Main;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void checkExist(String username){
        Boolean isExist = familyRepository.existsByUsername(username);
        if (isExist){
            throw new UsernameAlreadyExistsException("이미 사용중인 아이디입니다.");
        }
    }

    public void join(JoinRequest request){
        String username = request.getUsername();
        String password = request.getPassword();

        // 중복아이디 체크
        checkExist(username);

        // 아이디 유효성 체크 (영어, 숫자 포함 4-20자)
        if (!username.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new IllegalArgumentException("아이디는 영어와 숫자를 포함한 4-20자로 설정해야 합니다.");
        }

        // 비밀번호 유효성 체크 (영어, 숫자, 특수문자 포함 8-20자)
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,20}$")) {
            throw new IllegalArgumentException("비밀번호는 영어, 숫자, 특수문자를 포함한 6-20자로 설정해야 합니다.");
        }

        Family family = Family.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        familyRepository.save(family);

        // Quiz 테이블 생성
        Quiz quiz = Quiz.builder()
                .family(family)
                .build();
        quizRepository.save(quiz);

    }

    public List<ProfilesResponse> getProfiles(int familyId) {
        // familyId로 Member 조회
        List<Member> list = memberRepository.findByFamilyId(familyId);

        // 리스트로 리턴
        return list.stream()
                .map(ProfilesResponse::new)
                .collect(Collectors.toList());
    }

    public void addProfile(int familyId, AddProfileRequest request) {
        String name = request.getName();
        if (name.length() > 20 || name.isEmpty()){
            throw new IllegalArgumentException("이름은 20자 이내로 설정해야 합니다.");
        }
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 가족 계정을 찾을 수 없습니다.", 1));

        Member member = Member.builder()
                .name(name)
                .family(family)
                .build();
        memberRepository.save(member);

        // Quest 테이블 생성
        Quest quest = Quest.builder()
                .member(member)
                .build();
        questRepository.save(quest);
    }

    public String choiceMember(CustomUserDetails userDetails, ChoiceMemberRequest request, HttpServletResponse response) {
        int memberId = request.getMemberId();

        String newAccess = jwtUtil.createJwt("access", userDetails.getUsername(), "ROLE", userDetails.getFamilyId(), memberId, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userDetails.getUsername(), "ROLE", userDetails.getFamilyId(), memberId, 86400000L);

        addRefreshEntity(userDetails.getUsername(), newRefresh, 86400000L); // 기존의 refresh 토큰을 대체

        // 쿠키 생성 및 설정
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

    // Refresh 토큰을 데이터베이스에 저장하는 메소드
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.createRefresh(username, refresh, date.toString());

        refreshRepository.save(refreshEntity);
    }

    public MainProfileResponse getMainProfile(CustomUserDetails userDetails) {
        // img: Member 테이블
        // diary & quiz & word status: quest 테이블
        // shellCount: ShellService 에 getUserShell 갖다쓰기
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
}
