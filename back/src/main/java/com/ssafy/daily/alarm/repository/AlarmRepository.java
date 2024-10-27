package com.ssafy.daily.alarm.repository;

import com.ssafy.daily.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByFcmTokenId(int fcmTokensId);
}
