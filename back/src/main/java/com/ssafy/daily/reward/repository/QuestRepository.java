package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Integer> {
    Quest findByMemberId(int memberId);
    void deleteByMemberId(int memberId);
}
