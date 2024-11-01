package com.ssafy.daily.word.repository;

import com.ssafy.daily.word.entity.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {

    @Query("SELECT w FROM Word w WHERE NOT EXISTS (SELECT 1 FROM LearnedWord lw WHERE lw.word.id = w.id AND lw.member.id = :memberId)")
    List<Word> findUnlearnedWordsByMemberId(@Param("memberId") int memberId, Pageable pageable);
}

