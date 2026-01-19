package com.ssafy.daily.user.repository;

import com.ssafy.daily.reward.dto.ChildShellResponse;
import com.ssafy.daily.user.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    List<Member> findByFamilyId(int id);
    Optional<Member> findByIdAndFamilyId(int memberId, int familyId);
    boolean existsByFamilyIdAndName(int familyId, String name);
    Member findByFamilyIdAndName(int familyId, String name);

    @Modifying(clearAutomatically = true)
    @Query("""
      update Member m
         set m.shell = m.shell + :delta
       where m.id = :id
         and m.shell + :delta >= 0
    """)
    int updateShell(@Param("id") long id, @Param("delta") int delta);

    @Query("SELECT new com.ssafy.daily.reward.dto.ChildShellResponse(m.id, m.name, m.shell) " +
            "FROM Member m WHERE m.family.id = :familyId")
    List<ChildShellResponse> findChildShellsByFamilyId(@Param("familyId") int familyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdForUpdate(int id);
}
