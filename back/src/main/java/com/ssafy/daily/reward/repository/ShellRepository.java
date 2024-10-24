package com.ssafy.daily.reward.repository;

import com.ssafy.daily.reward.entity.Shell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShellRepository extends JpaRepository<Shell, Integer> {
}
