package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Shell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShellRepository extends JpaRepository<Shell, Long> {

    // 특정 Member의 stock 총합을 구하는 JPQL 쿼리
    @Query("SELECT SUM(s.stock) FROM Shell s WHERE s.member.id = :memberId")
    Integer findTotalStockByMemberId(@Param("memberId") int memberId);

    void deleteByMemberId(int memberId);
}
