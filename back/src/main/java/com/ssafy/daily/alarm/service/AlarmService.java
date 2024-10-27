package com.ssafy.daily.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.daily.alarm.dto.AlarmResponse;
import com.ssafy.daily.alarm.dto.CheckAlarmRequest;
import com.ssafy.daily.alarm.dto.SaveTokenRequest;
import com.ssafy.daily.alarm.entity.Alarm;
import com.ssafy.daily.alarm.entity.FCMToken;
import com.ssafy.daily.alarm.repository.AlarmRepository;
import com.ssafy.daily.alarm.repository.FCMTokenRepository;
import com.ssafy.daily.common.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AlarmService {
    private final FCMTokenRepository fcmTokenRepository;
    private final AlarmRepository alarmRepository;

    // 토큰 저장
    public void saveToken(SaveTokenRequest request) {
        
        // 임시 데이터
        int id = 1;
        Role role = Role.CHILD;

        // 기존 토큰 조회
        FCMToken existingToken = fcmTokenRepository.findByUserIdAndRole(id, role);

        if (existingToken != null) {
            // 기존 토큰이 있으면 업데이트
            existingToken.update(request.getToken());
            fcmTokenRepository.save(existingToken);
        } else {
            // 기존 토큰이 없으면 새로 생성
            FCMToken newToken = FCMToken.builder()
                    .userId(id)
                    .token(request.getToken())
                    .role(role)
                    .build();
            fcmTokenRepository.save(newToken);
        }
    }

    // 알림 전송
    /*
    titleId : 수락이나 확인 누를 시 이동할 페이지에 필요한 id (그림 일기의 id or 그림 퀴즈 sessionId)
    userId : 받는 사람의 id(familyId or memberId)
    name : 보내는 사람의 이름
    role : 받는 사람의 role(PARENT or CHILD)
    title : 알림 제목 (그림 일기 or 그림 퀴즈)
    body : 알림 내용 ex) 그림 퀴즈 요청
    */
    public void sendNotification(String name, int titleId, int toId, Role role, String title, String body) throws Exception {
        // 토큰 조회
        FCMToken fcmToken = getToken(toId, role);

        // 알림 메시지
        String token = fcmToken.getToken();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(name + " 님의 " + title)
                        .setBody(body)
                        .build())
                .build();

        // 알림 전송
        FirebaseMessaging.getInstance().send(message);

        // 알림 저장
        saveAlarm(name, titleId, fcmToken, title, body);
    }

    // 알림 조회
    public List<AlarmResponse> getAlarms() {
        // 임시 데이터
        Role role = Role.CHILD;
        int userId = 1;
        
        // 알림 조회
        FCMToken fcmToken = getToken(userId, role);
        List<Alarm> list = alarmRepository.findByFcmTokenId(fcmToken.getId());

        return list.stream()
                .map(AlarmResponse::new)
                .collect(Collectors.toList());
    }

    // 알림 확인
    public void checkAlarm(CheckAlarmRequest request) {
        Alarm alarm = alarmRepository.findById(request.getAlarmId())
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 알림을 찾을 수 없습니다.", 1));
        alarm.confirm();
        alarmRepository.save(alarm);
    }

    // 토큰 조회
    private FCMToken getToken(int userId, Role role) {
        FCMToken fcmTokens = fcmTokenRepository.findByUserIdAndRole(userId, role);;
        if (fcmTokens == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        return fcmTokens;
    }

    // 알림 저장
    private void saveAlarm(String name, int titleId, FCMToken fcmToken, String title, String body) {
        Alarm alarm = Alarm.builder()
                .titleId(titleId)
                .fcmToken(fcmToken)
                .title(title)
                .body(body)
                .name(name)
                .build();
        alarmRepository.save(alarm);
    }
}
