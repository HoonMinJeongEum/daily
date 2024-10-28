package com.ssafy.daily.word.repository;

import com.ssafy.daily.word.entity.LearnedWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearnedWordRepository extends JpaRepository<LearnedWord, Long> {
    List<LearnedWord> findByMemberId(int memberId);
}
