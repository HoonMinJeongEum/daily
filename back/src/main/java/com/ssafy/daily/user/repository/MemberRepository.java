package com.ssafy.daily.user.repository;

import com.ssafy.daily.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    List<Member> findByFamilyId(int id);
    Optional<Member> findByIdAndFamilyId(int memberId, int familyId);
    boolean existsByFamilyIdAndName(int familyId, String name);
    Member findByFamilyIdAndName(int familyId, String name);
}
