package com.ssafy.daily.alarm.repository;

import com.ssafy.daily.alarm.entity.Alarm;
import com.ssafy.daily.alarm.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByFcmTokenId(int fcmTokensId);
    List<Alarm> findByTitleAndTitleIdAndConfirmedAtIsNull(String title, String titleId);
    @Query("SELECT a FROM Alarm a " +
            "WHERE a.fcmToken.id = :fcmTokenId " +
            "ORDER BY CASE WHEN a.confirmedAt IS NULL THEN 0 ELSE 1 END, " +
            "CASE WHEN a.confirmedAt IS NULL THEN a.createdAt END DESC, " +
            "a.confirmedAt DESC")
    List<Alarm> findByFcmTokenIdWithSorting(@Param("fcmTokenId") int fcmTokenId);
    void deleteByNameAndFcmToken_Id(String name, int fcmTokenId);
    @Modifying
    @Transactional
    @Query("UPDATE Alarm a SET a.name = :newName WHERE a.name = :oldName AND a.fcmToken.id = :fcmTokenId")
    void updateAlarmNameByFcmToken(
            @Param("oldName") String oldName,
            @Param("newName") String newName,
            @Param("fcmTokenId") int fcmTokenId
    );

}
