package com.ssafy.daily.word.service;

import com.ssafy.daily.diary.dto.FieldDto;
import com.ssafy.daily.diary.service.DiaryService;
import com.ssafy.daily.exception.EmptyOcrResultException;
import com.ssafy.daily.exception.S3UploadException;
import com.ssafy.daily.exception.WordMismatchException;
import com.ssafy.daily.file.service.S3UploadService;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.word.dto.LearnedWordResponse;
import com.ssafy.daily.word.dto.LearningWordResponse;
import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.word.entity.LearnedWord;
import com.ssafy.daily.word.entity.Word;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.word.repository.LearnedWordRepository;
import com.ssafy.daily.word.repository.WordRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final LearnedWordRepository learnedWordRepository;
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;
    private final S3UploadService s3UploadService;
    private final DiaryService diaryService;

    @Value(("${clova.ocr.apiUrl}"))
    private String apiUrl;

    @Value("${clova.ocr.secretKey}")
    private String secretKey;

    public List<LearnedWordResponse> getLearnedWordsByMember(int memberId) {
        List<LearnedWord> learnedWords = learnedWordRepository.findByMemberId(memberId);
        return learnedWords.stream()
                .map(word -> new LearnedWordResponse(
                        word.getId(),
                        word.getWord().getWord(),
                        word.getImg(),
                        word.getWord().getImg(),
                        word.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<LearnedWordResponse> getChildLearnedWordsWithParentCheck(CustomUserDetails userDetails, int childId) {
        if (!isParentOfChild(userDetails, childId)) {
            return null;
        }

        return getLearnedWordsByMember(childId);
    }

    private boolean isParentOfChild(CustomUserDetails userDetails, int childId) {
        Optional<Member> optionalMember = memberRepository.findById(childId);

        if (optionalMember.isPresent()) {
            Member child = optionalMember.get();
            int childFamilyId = child.getFamily().getId();
            int parentFamilyId = userDetails.getFamilyId();

            return parentFamilyId == childFamilyId;
        }
        return false;
    }

    public List<LearningWordResponse> getUnlearnedWords(int memberId) {
        List<Word> unlearnedWords = wordRepository.findUnlearnedWordsByMemberId(memberId, PageRequest.of(0, 5));
        return unlearnedWords.stream()
                .map(word -> new LearningWordResponse(word.getId(), word.getWord(), word.getImg()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markWordsAsLearned(int memberId, List<Integer> ids, List<MultipartFile> files) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버id가 없습니다: " + memberId));

        if (ids.size() != files.size()) {
            throw new IllegalArgumentException("ID와 파일의 개수가 일치하지 않습니다.");
        }

        List<LearnedWord> learnedWords = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            int wordId = ids.get(i);
            MultipartFile file = files.get(i);

            Word word = wordRepository.findById(wordId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 단어id가 없습니다: " + wordId));

            String imageUrl;
            try {
                imageUrl = s3UploadService.saveFile(file);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
            }

            LearnedWord learnedWord = LearnedWord.builder()
                    .member(member)
                    .word(word)
                    .img(imageUrl)
                    .build();
            learnedWords.add(learnedWord);
        }

        learnedWordRepository.saveAll(learnedWords);
    }

    public StatusResponse checkSimilarity(String word, MultipartFile writeFile) {
        if (writeFile == null || writeFile.isEmpty()) {
            log.error("유효하지 않은 파일입니다.");
            throw new IllegalArgumentException("파일이 유효하지 않습니다.");
        }

        String writeUrl = null;
        try {
            writeUrl = s3UploadService.saveFile(writeFile);
        } catch (IOException e) {
            log.error("S3 업로드 실패", e);
            throw new S3UploadException("S3 작성한 단어 이미지 업로드 실패");
        }

        String content = diaryService.processOcr(writeUrl);

        String orgImg = s3UploadService.getFileNameFromUrl(writeUrl);
        s3UploadService.deleteImage(orgImg);

        String resultWord = content.trim().replaceAll("\\s+", "");

        if (resultWord.isEmpty()) {
            log.warn("OCR 결과가 비어 있습니다.");
            throw new EmptyOcrResultException("인식된 단어가 없습니다. 단어를 작성해주세요.");
        }

        if (!resultWord.equals(word)) {
            log.info("단어 일치 실패: 입력한 단어 '{}'와 OCR 결과 '{}'가 일치하지 않습니다.", word, resultWord);
            throw new WordMismatchException(resultWord + "의 유사도가 낮습니다. 다시 시도해주세요!");
        }

        log.info("단어가 성공적으로 일치합니다: '{}'", word);
        return new StatusResponse(200, "단어가 정확하게 작성되었습니다!");
    }
}
