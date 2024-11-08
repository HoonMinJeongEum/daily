package com.ssafy.daily.quiz.repository;

import com.ssafy.daily.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Quiz findByMemberId(int memberId);
    Quiz findBySessionId(String sessionId);

    void deleteByMemberId(int memberId);
}
