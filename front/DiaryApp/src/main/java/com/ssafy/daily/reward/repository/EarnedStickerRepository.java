package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.EarnedSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EarnedStickerRepository extends JpaRepository<EarnedSticker, Long> {

    // memberId로 EarnedSticker 조회하는 메서드
    List<EarnedSticker> findByMemberId(int memberId);

    // memberId와 stickerId로 EarnedSticker 존재 여부 확인하는 메서드
    boolean existsByMemberIdAndStickerId(int memberId, long stickerId);

    void deleteByMemberId(int memberId);
}
