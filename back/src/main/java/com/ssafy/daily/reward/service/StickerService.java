package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.InsufficientFundsException;
import com.ssafy.daily.exception.StickerNotFoundException;
import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.entity.*;
import com.ssafy.daily.reward.repository.EarnedStickerRepository;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.reward.repository.StickerRepository;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StickerService {

    private final StickerRepository stickerRepository;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final ShellRepository shellRepository;
    private final EarnedStickerRepository earnedStickerRepository;

    public List<EarnedStickerResponse> getUserSticker() {

        // 멤버 있는지 확인
        int memberId = 1;

        // memberId로 EarnedSticker 리스트 조회
        List<EarnedSticker> list = earnedStickerRepository.findByMemberId(memberId);

        // EarnedSticker 리스트를 StickerResponse 리스트로 변환
        return list.stream()
                .map(earnedSticker -> new EarnedStickerResponse(earnedSticker.getSticker()))
                .collect(Collectors.toList());
    }

    public List<StickerResponse> getSticker() {

        // 멤버 있는지 확인
        int memberId = 1;

        // 멤버 ID를 기준으로 획득하지 않은 스티커만 조회
        List<Sticker> list = stickerRepository.findUnownedStickersByMemberId(memberId);

        // StickerResponse로 변환하여 반환
        return list.stream()
                .map(StickerResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void buySticker(BuyStickerRequest request) {

        // 멤버 있는지 확인
        int memberId = 1; // 임의 데이터
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("구성원을 찾을 수 없습니다."));

        // 스티커 있는지 확인
        Sticker sticker = stickerRepository.findById(request.getStickerId())
                .orElseThrow(() -> new StickerNotFoundException("해당 스티커를 찾을 수 없습니다."));

        // 멤버가 스티커를 이미 가지고 있는지 확인
        boolean exists = earnedStickerRepository.existsByMemberIdAndStickerId(member.getId(), sticker.getId());
        if (exists) {
            throw new AlreadyOwnedException("이미 소유한 스티커입니다.");
        }

        // 조개가 충분한지 확인
        int shellCount = calculateTotalStockForMember(memberId);
        if (shellCount < sticker.getPrice()) {
            throw new InsufficientFundsException("재화 수량이 부족합니다.");
        }

        // EarnedSticker 엔티티 생성 및 저장
        EarnedSticker earnedSticker = EarnedSticker.builder()
                .sticker(sticker)
                .member(member)
                .build();
        earnedStickerRepository.save(earnedSticker);

        // Shell 로그 남기기 (조개 수량 차감 로그)
        Shell shellLog = Shell.builder()
                .member(member)
                .stock((byte) (-sticker.getPrice()))
                .content(Content.STICKER)
                .lastUpdated(LocalDateTime.now())
                .build();
        shellRepository.save(shellLog);
    }

    public int calculateTotalStockForMember(int memberId) {
        Integer totalStock = shellRepository.findTotalStockByMemberId(memberId);
        return totalStock != null ? totalStock : 0;
    }
}
