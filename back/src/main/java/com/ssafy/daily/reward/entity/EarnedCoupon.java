package com.ssafy.daily.reward.entity;

import com.ssafy.daily.exception.AlreadyOwnedException;
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
public class EarnedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column
    private LocalDateTime usedAt;

    /**
     * 쿠폰 사용
     * @param usedAt 사용 날짜
     */
    public void use(LocalDateTime usedAt) {
        if(this.getUsedAt() != null) {
            throw new AlreadyOwnedException("이미 사용된 쿠폰 입니다.");
        }
        this.usedAt = usedAt;
    }
}
