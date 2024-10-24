package com.ssafy.daily.reward.entity.id;

import java.io.Serializable;
import java.util.Objects;

public class EarnedStickerId implements Serializable {

    private int stickerId;
    private int memberId;

    public EarnedStickerId() {}

    public EarnedStickerId(int stickerId, int memberId) {
        this.stickerId = stickerId;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EarnedStickerId that = (EarnedStickerId) o;
        return stickerId == that.stickerId && memberId == that.memberId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stickerId, memberId);
    }
}
