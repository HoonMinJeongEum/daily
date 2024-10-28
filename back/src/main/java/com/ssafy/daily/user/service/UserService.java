package com.ssafy.daily.user.service;

import com.ssafy.daily.exception.UsernameAlreadyExistsException;
import com.ssafy.daily.user.dto.*;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.jwt.JWTUtil;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

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
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")) {
            throw new IllegalArgumentException("비밀번호는 영어, 숫자, 특수문자를 포함한 8-20자로 설정해야 합니다.");
        }

        Family family = Family.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        familyRepository.save(family);
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
    }

    public String choiceMember(CustomUserDetails userDetails, ChoiceMemberRequest request) {
        int memberId = request.getMemberId();
        return jwtUtil.createJwt("access", userDetails.getUsername(), "ROLE", userDetails.getId(), memberId, 600000L);
    }
}
