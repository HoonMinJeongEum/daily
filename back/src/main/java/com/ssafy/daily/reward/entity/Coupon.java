package com.ssafy.daily.reward.entity;

import com.ssafy.daily.exception.AlreadyOwnedException;
import com.ssafy.daily.user.entity.Family;
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
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column
    private LocalDateTime purchasedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 쿠폰 구매
     * @param purchasedAt 구매 날짜
     */
    public void buy(LocalDateTime purchasedAt) {
        if (this.getPurchasedAt() != null) {
            throw new AlreadyOwnedException("이미 구매한 쿠폰입니다.");
        }
        this.purchasedAt = purchasedAt;
    }

    /**
     * 쿠폰 삭제 가능 여부 체크 후 삭제
     */
    public void validateDeletable() {
        if (this.purchasedAt != null) {
            throw new AlreadyOwnedException("이미 구매한 쿠폰은 삭제할 수 없습니다.");
        }
    }
}
