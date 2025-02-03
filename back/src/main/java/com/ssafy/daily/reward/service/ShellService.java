package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.exception.InsufficientFundsException;
import com.ssafy.daily.exception.MyNotFoundException;
import com.ssafy.daily.reward.dto.ChildShellResponse;
import com.ssafy.daily.reward.entity.Shell;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ShellService {
    private final ShellRepository shellRepository;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;

    /**
     * 자식들의 조개 개수 조회
     */
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

    /**
     * 사용자 소유 조개 개수 반환
     */
    public int getUserShell(int memberId) {
        Integer totalStock = shellRepository.findTotalStockByMemberId(memberId);
        return totalStock != null ? totalStock : 0;
    }

    /**
     * Shell 로그 저장
     */
    @Transactional
    public void saveShellLog(Member member, int stock, Content content) {
        shellRepository.save(Shell.builder()
                .member(member)
                .stock(stock)
                .content(content)
                .lastUpdated(LocalDateTime.now())
                .build());
    }

    /**
     * 잔액 검증
     */
    public void validateShellBalance(int memberId, int stickerPrice) {
        int shellCount = getUserShell(memberId);
        if (shellCount < stickerPrice) {
            throw new InsufficientFundsException("재화가 부족합니다.");
        }
    }

    /**
     * 프로필 존재 여부 검증
     */
    public Member validateMember(int memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MyNotFoundException("존재하지 않는 프로필입니다."));
    }

    /**
     * 계정 존재 여부 검증
     */
    public Family validateFamily(int familyId) {
        return familyRepository.findById(familyId)
                .orElseThrow(() -> new MyNotFoundException("해당 가족 계정을 찾을 수 없습니다."));
    }
}
