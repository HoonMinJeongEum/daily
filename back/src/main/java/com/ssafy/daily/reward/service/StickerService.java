package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.entity.*;
import com.ssafy.daily.reward.repository.EarnedStickerRepository;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.reward.repository.StickerRepository;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
    private final ShellRepository shellRepository;
    private final EarnedStickerRepository earnedStickerRepository;

    public List<EarnedStickerResponse> getUserSticker() {

        // memberId 가져오기 (예: 임의 값)
        int memberId = 1;

        // memberId로 EarnedSticker 리스트 조회
        List<EarnedSticker> list = earnedStickerRepository.findByMemberId(memberId);

        // EarnedSticker 리스트를 StickerResponse 리스트로 변환
        return list.stream()
                .map(earnedSticker -> new EarnedStickerResponse(earnedSticker.getSticker()))  // EarnedStickerResponse로 변환
                .collect(Collectors.toList());
    }

    public List<StickerResponse> getSticker() {
        List<Sticker> list = stickerRepository.findAll();

        // StickerResponse로 변환
        return list.stream()
                .map(StickerResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public String buySticker(BuyStickerRequest request) {

        // memberId 가져오기
        int memberId = 1; // 임의 데이터

        // 멤버 있는지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        // 스티커 있는지 확인
        Sticker sticker = stickerRepository.findById(request.getStickerId())
                .orElseThrow(() -> new RuntimeException("스티커를 찾을 수 없습니다."));

        // 멤버가 스티커를 이미 가지고 있는지 확인
//        boolean exists = earnedStickerRepository.existsById(memberId);
//        if (exists) {
//            return "해당 스티커를 이미 보유하고 있습니다.";
//        }

        // 조개가 충분한지 확인
        int shellCount = calculateTotalStockForMember(memberId);
        if (shellCount < sticker.getPrice()) {
            return "조개 수량이 부족합니다.";
        }

//        // EarnedSticker 엔티티 생성 및 저장
//        EarnedSticker earnedSticker = EarnedSticker.builder()
//                .sticker(sticker)
//                .memberId(member.getId())
//                .createAt(LocalDateTime.now())
//                .build();
//        earnedStickerRepository.save(earnedSticker);

        // Shell 로그 남기기 (조개 수량 차감 로그)
        Shell shellLog = Shell.builder()
                .member(member)
                .stock((byte) (-sticker.getPrice()))
                .content(Content.STICKER)
                .lastUpdated(LocalDateTime.now())
                .build();
        shellRepository.save(shellLog);

        return "스티커가 정상적으로 구매되었습니다.";
    }

    public int calculateTotalStockForMember(int memberId) {
        Integer totalStock = shellRepository.findTotalStockByMemberId(memberId);
        return totalStock != null ? totalStock : 0;  // null인 경우 0으로 처리
    }
}
