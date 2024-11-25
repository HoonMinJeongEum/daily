package com.ssafy.daily.quiz.entity;

import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private String sessionId;

    @Column
    private LocalDateTime endAt;

    public void updateEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public void updateSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
