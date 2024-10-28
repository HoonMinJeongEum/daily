package com.ssafy.daily.quiz.repository;

import com.ssafy.daily.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Quiz findByFamilyId(int familyId);
}
