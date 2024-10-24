package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.EarnedSticker;
import com.ssafy.daily.reward.entity.id.EarnedStickerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarnedStickerRepository extends JpaRepository<EarnedSticker, EarnedStickerId> {

    // memberId로 EarnedSticker 조회하는 메서드
    List<EarnedSticker> findByMemberId(int memberId);
}
