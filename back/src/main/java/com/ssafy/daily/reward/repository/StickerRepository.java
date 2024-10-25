package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Integer> {

    // 특정 멤버가 획득하지 않은 스티커만 조회
    @Query("SELECT s FROM Sticker s WHERE s.id NOT IN " +
            "(SELECT e.sticker.id FROM EarnedSticker e WHERE e.member.id = :memberId)")
    List<Sticker> findUnownedStickersByMemberId(@Param("memberId") int memberId);
}
