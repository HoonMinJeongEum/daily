package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.exception.StickerNotFoundException;
import com.ssafy.daily.reward.dto.BuyStickerRequest;
import com.ssafy.daily.reward.dto.EarnedStickerResponse;
import com.ssafy.daily.reward.dto.StickerResponse;
import com.ssafy.daily.reward.entity.EarnedSticker;
import com.ssafy.daily.reward.entity.Sticker;
import com.ssafy.daily.reward.repository.EarnedStickerRepository;
import com.ssafy.daily.reward.repository.StickerRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StickerService {
    private final StickerRepository stickerRepository;
    private final EarnedStickerRepository earnedStickerRepository;
    private final ShellService shellService;

    /**
     * 사용자가 보유한 스티커 목록 조회
     */
    public List<EarnedStickerResponse> getUserSticker(CustomUserDetails userDetails) {
        int memberId = userDetails.getMember().getId();
        List<EarnedSticker> list = earnedStickerRepository.findByMemberId(memberId);
        return list.stream()
                .map(earnedSticker -> new EarnedStickerResponse(earnedSticker.getSticker()))
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 획득하지 않은 스티커 목록 조회
     */
    public List<StickerResponse> getSticker(CustomUserDetails userDetails) {
        int memberId = userDetails.getMember().getId();
        List<Sticker> list = stickerRepository.findUnownedStickersByMemberId(memberId);
        return list.stream()
                .map(StickerResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 스티커 구매
     */
    @Transactional
    public int buySticker(CustomUserDetails userDetails, BuyStickerRequest request) {
        Member member = shellService.validateMember(userDetails.getMember().getId());
        Sticker sticker = validateSticker(request.getStickerId());
        validateOwnership(member.getId(), sticker.getId());
        shellService.validateShellBalance(member.getId(), sticker.getPrice());

        EarnedSticker earnedSticker = EarnedSticker.builder()
                .sticker(sticker)
                .member(member)
                .build();
        earnedStickerRepository.save(earnedSticker);

        // Shell 차감
        shellService.saveShellLog(member, -sticker.getPrice(), Content.STICKER);

        return shellService.getUserShell(member.getId());
    }

    /**
     * 스티커 존재 여부 검증
     */
    private Sticker validateSticker(int stickerId) {
        return stickerRepository.findById(stickerId)
                .orElseThrow(() -> new StickerNotFoundException("해당 스티커를 찾을 수 없습니다."));
    }

    /**
     * 이미 보유한 스티커인지 검증
     */
    private void validateOwnership(int memberId, int stickerId) {
        boolean exists = earnedStickerRepository.existsByMemberIdAndStickerId(memberId, stickerId);
        if (exists) {
            throw new AlreadyOwnedException("이미 소유한 스티커입니다.");
        }
    }
}
