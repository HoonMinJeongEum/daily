package com.ssafy.daily.reward.entity;
import com.ssafy.daily.reward.entity.id.EarnedStickerId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@IdClass(EarnedStickerId.class)  // 복합 키로 사용할 클래스를 지정
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EarnedSticker {

    @Id
    @Column(nullable = false)
    private int stickerId;

    @Id
    @Column(nullable = false)
    private int memberId;

    @ManyToOne(fetch = FetchType.LAZY)  // Sticker와 다대일 관계 설정
    @JoinColumn(name = "stickerId", insertable = false, updatable = false)  // 외래 키 설정
    private Sticker sticker;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;
}
