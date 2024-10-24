package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Integer> {
}
