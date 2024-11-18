package com.ssafy.daily.reward.entity;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Content content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    public void prePersist() {
        this.lastUpdated = LocalDateTime.now();
    }
}
