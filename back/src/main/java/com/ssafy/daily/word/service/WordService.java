package com.ssafy.daily.word.service;

import com.ssafy.daily.file.service.S3UploadService;
import com.ssafy.daily.word.dto.CompleteLearningRequest;
import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.dto.LearningWordResponse;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.entity.Word;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import com.ssafy.daily.word.repository.WordRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final LearnedWordRepository learnedWordRepository;
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;
    private final S3UploadService s3UploadService;

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

    public List<LearningWordResponse> getUnlearnedWords(int memberId) {
        List<Word> unlearnedWords = wordRepository.findUnlearnedWordsByMemberId(memberId, PageRequest.of(0, 10));
        return unlearnedWords.stream()
                .map(word -> new LearningWordResponse(word.getId(), word.getWord(), word.getImg()))
                .collect(Collectors.toList());
    }

    public void markWordsAsLearned(int memberId, List<CompleteLearningRequest> words) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버id가 없습니다: " + memberId));

        List<LearnedWord> learnedWords = words.stream()
                .map(wordReq -> {
                    Word word = wordRepository.findById(wordReq.getId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 단어id가 없습니다: " + wordReq.getId()));

                    String imageUrl = null;
                    try {
                        imageUrl = s3UploadService.saveFile(wordReq.getImage());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return LearnedWord.builder()
                            .member(member)
                            .word(word)
                            .img(imageUrl)
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        learnedWordRepository.saveAll(learnedWords);
    }
}
