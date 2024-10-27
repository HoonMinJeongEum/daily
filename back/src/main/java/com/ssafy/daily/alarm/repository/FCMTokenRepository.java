package com.ssafy.daily.alarm.repository;

import com.ssafy.daily.alarm.entity.FCMToken;
import com.ssafy.daily.common.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
    FCMToken findByUserIdAndRole(int userId, Role role);
}
