package com.ssafy.daily.word.service;

import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearnedWordService {

    private final LearnedWordRepository learnedWordRepository;

    public List<LearnedWordResponse> getLearnedWordsByMember(int memberId) {
        List<LearnedWord> learnedWords = learnedWordRepository.findByMemberId(memberId);
        return learnedWords.stream()
                .map(word -> new LearnedWordResponse(
                        word.getId(),
                        word.getWord().getWord(),
                        word.getImg(),
                        word.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
