package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.reward.dto.ChildShellResponse;
import com.ssafy.daily.reward.entity.Shell;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShellService {
    private final ShellRepository shellRepository;
    private final MemberRepository memberRepository;

    // 자식들 조개 조회
    public List<ChildShellResponse> getChildShells(CustomUserDetails userDetails) {
        int familyId = userDetails.getFamily().getId();

        return memberRepository.findByFamilyId(familyId).stream()
                .map(member -> {
                    int totalShellCount = Optional.ofNullable(shellRepository.findTotalStockByMemberId(member.getId()))
                            .orElse(0);
                    return new ChildShellResponse(member, totalShellCount);
                })
                .collect(Collectors.toList());
    }

    // 사용자 소유 조개 수량 반환
    public int getUserShell(int memberId) {
        Integer totalStock = shellRepository.findTotalStockByMemberId(memberId);
        return totalStock != null ? totalStock : 0;
    }

    // Shell 로그 저장
    public void saveShellLog(Member member, int stock, Content content) {
        Shell shellLog = Shell.builder()
                .member(member)
                .stock(stock)
                .content(content)
                .lastUpdated(LocalDateTime.now())
                .build();
        shellRepository.save(shellLog);
    }
}
