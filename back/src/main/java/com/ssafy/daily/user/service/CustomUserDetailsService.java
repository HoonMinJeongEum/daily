package com.ssafy.daily.user.service;

import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Family familyData = familyRepository.findByUsername(username);
        if (familyData != null) {
            System.out.println("UserDetailsService - Family ID: " + familyData.getId()); // ID 확인
            return new CustomUserDetails(familyData, null);
        }
        // Member 조회 (부모가 선택한 자녀가 있는 경우)
        try {
            int memberId = Integer.parseInt(username); // username를 memberId로 변환
            Member memberData = memberRepository.findById(memberId)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + memberId)); // Member ID로 찾기
            System.out.println("UserDetailsService - Member ID: " + memberData.getId()); // ID 확인
            return new CustomUserDetails(null, memberData); // Member 정보로 CustomUserDetails 생성
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid identifier: " + username);
        }
    }
}
