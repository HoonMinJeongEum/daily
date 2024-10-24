package com.ssafy.daily.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne  // 여러 Shell이 하나의 Member에 속함
    @JoinColumn(name = "member_id")  // Shell 테이블에 외래 키로 저장
    private Member member;  // Member와의 다대일 관계

    @Column(nullable = false)
    private byte stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Content content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lastUpdated;

}
