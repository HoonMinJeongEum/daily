package com.ssafy.daily.reward.service;


import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.StatusResponse;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.entity.*;
import com.ssafy.daily.reward.entity.id.EarnedStickerId;
import com.ssafy.daily.reward.repository.EarnedStickerRepository;
import com.ssafy.daily.reward.repository.MemberRepository;
import com.ssafy.daily.reward.repository.ShellRepository;
import com.ssafy.daily.reward.repository.StickerRepository;
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
    public StatusResponse buySticker(BuyStickerRequest request) {

        // memberId 가져오기
        int memberId = 1; // 임의 데이터

        // 멤버 있는지 확인
        Member member = memberRepository.findById(memberId).orElseThrow();
    
        // 스티커 있는지 확인
        Sticker sticker = stickerRepository.findById(request.getStickerId()).orElseThrow();

        // 복합 키 객체 생성
        EarnedStickerId earnedStickerId = new EarnedStickerId(sticker.getId(), member.getId());

        // 멤버가 스티커를 이미 가지고 있는지 확인
        boolean exists = earnedStickerRepository.existsById(earnedStickerId);

        // 스티커를 이미 보유하고 있을 때
        if (exists) {
            return new StatusResponse(400, "해당 스티커를 이미 보유하고 있습니다.");
        }

        // 조개가 충분한지 확인
        // shellLog 리스트에서 각 로그의 값을 합산하여 조개 수량을 계산하는 예시
        int shellCount = member.getShellLog()
                .stream()
                .mapToInt(Shell::getStock)  // 각 Shell 로그의 stock 값을 가져옴
                .sum();  // 합산하여 조개 수량 계산
        if (shellCount < sticker.getPrice()) {
            return new StatusResponse(400, "조개 수량이 부족합니다.");
        }

        // EarnedSticker 엔티티 생성 및 저장
        EarnedSticker earnedSticker = EarnedSticker.builder()
                .stickerId(sticker.getId())
                .memberId(member.getId())
                .createAt(LocalDateTime.now())
                .build();
        earnedStickerRepository.save(earnedSticker);

        // Shell 로그 남기기 (조개 수량 차감 로그)
        Shell shellLog = Shell.builder()
                .member(member)
                .stock((byte) (-sticker.getPrice()))  // 스티커 가격만큼 차감
                .content(Content.STICKER)  // 스티커
                .lastUpdated(LocalDateTime.now())  // 로그 시간 기록
                .build();
        shellRepository.save(shellLog);  // 로그 저장
        return new StatusResponse(200, "스티커가 정상적으로 구매되었습니다.");
    }



}
