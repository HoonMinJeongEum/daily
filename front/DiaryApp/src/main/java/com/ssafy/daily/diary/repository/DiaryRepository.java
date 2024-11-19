package com.ssafy.daily.diary.repository;

import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findByMemberId(int id);

    void deleteByMemberId(int memberId);

    @Query("SELECT d FROM Diary d WHERE d.member.id = :memberId AND YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month")
    List<Diary> findByMemberIdAndYearAndMonth(@Param("memberId") int memberId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT d FROM Diary d WHERE d.member.id = :memberId AND DATE(d.createdAt) = :today")
    Optional<Diary> findByMemberIdAndDate(@Param("memberId") int memberId, @Param("today") LocalDate today);
}
