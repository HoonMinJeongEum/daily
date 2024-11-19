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
            return new CustomUserDetails(familyData, null);
        }
        try {
            int memberId = Integer.parseInt(username);
            Member memberData = memberRepository.findById(memberId)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + memberId));
            return new CustomUserDetails(familyData, memberData);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid identifier: " + username);
        }
    }
}
