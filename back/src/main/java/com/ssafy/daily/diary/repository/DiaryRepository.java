package com.ssafy.daily.diary.repository;

import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findByMemberId(int id);
}
