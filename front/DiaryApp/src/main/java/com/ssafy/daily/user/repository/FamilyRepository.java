package com.ssafy.daily.user.repository;

import com.ssafy.daily.user.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {
    Boolean existsByUsername(String username);
    Family findByUsername(String username);
}
