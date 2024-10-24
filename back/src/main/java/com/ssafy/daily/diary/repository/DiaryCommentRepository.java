package com.ssafy.daily.diary.repository;

import com.ssafy.daily.diary.entity.DiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryCommentRepository extends JpaRepository<DiaryComment, Integer> {
}
