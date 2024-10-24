package com.ssafy.daily.word.repository;

import com.ssafy.daily.word.entity.LearnedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearnedWordRepository extends JpaRepository<LearnedWord, Long> {
}
