package com.ssafy.daily.diary.repository;

import com.ssafy.daily.diary.entity.Diary;
import com.ssafy.daily.diary.entity.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Integer> {
    List<DiaryComment> findByDiaryId(int id);

    void deleteByDiaryId(int id);
}
