package com.ssafy.daily.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.daily.alarm.dto.AlarmListResponse;
import com.ssafy.daily.alarm.dto.AlarmResponse;
import com.ssafy.daily.alarm.dto.CheckAlarmRequest;
import com.ssafy.daily.alarm.dto.SaveTokenRequest;
import com.ssafy.daily.alarm.entity.Alarm;
import com.ssafy.daily.alarm.entity.FCMToken;
import com.ssafy.daily.alarm.repository.AlarmRepository;
import com.ssafy.daily.alarm.repository.FCMTokenRepository;
import com.ssafy.daily.common.Role;
import com.ssafy.daily.common.StatusResponse;
import com.ssafy.daily.user.dto.CustomUserDetails;
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
    public StatusResponse saveToken(CustomUserDetails userDetails, SaveTokenRequest request) {

        int id = userDetails.getMember() == null ? userDetails.getFamily().getId() : userDetails.getMember().getId();
        Role role = userDetails.getMember() == null ? Role.PARENT : Role.CHILD;
        System.out.println("FCM Role: " + role);
        System.out.println("FCM Id: " + id);
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
        return new StatusResponse(200, "알림 토큰이 정상적으로 등록되었습니다.");
    }

    // 알림 전송
    /*
    name : 보내는 사람의 이름
    titleId : 수락이나 확인 누를 시 이동할 페이지에 필요한 id (그림 일기의 id or 그림 퀴즈 sessionId)
    toId : 받는 사람의 Id
    role : 받는 사람의 role(PARENT or CHILD)
    title : 알림 제목 (그림 일기 or 그림 퀴즈)
    body : 알림 내용 ex) 그림 퀴즈 요청
    */
    public void sendNotification(String name, String titleId, int toId, Role role, String title, String body) throws Exception {
        // 토큰 조회
        FCMToken fcmToken = getToken(toId, role);

        // 알림 메시지
        String token = fcmToken.getToken();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("title", title)
                .putData("titleId", titleId)
                .putData("name", name)
                .build();

        // 알림 전송
        FirebaseMessaging.getInstance().send(message);

        // 알림 저장
        saveAlarm(name, titleId, fcmToken, title, body);
    }

    // 알림 조회
    public AlarmListResponse getAlarms(CustomUserDetails userDetails) {
        int id = userDetails.getMember() == null ? userDetails.getFamily().getId() : userDetails.getMember().getId();
        Role role = userDetails.getMember() == null ? Role.PARENT : Role.CHILD;
        
        // 알림 조회
        FCMToken fcmToken = getToken(id, role);
        List<Alarm> list = alarmRepository.findByFcmTokenIdWithSorting(fcmToken.getId());

        List<AlarmResponse> limitedAlarms = list.stream()
                .limit(15)
                .map(AlarmResponse::new)
                .collect(Collectors.toList());

        return new AlarmListResponse(limitedAlarms);
    }

    // 알림 확인
    public StatusResponse checkAlarm(CheckAlarmRequest request) {
        Alarm alarm = alarmRepository.findById(request.getAlarmId())
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 알림을 찾을 수 없습니다.", 1));
        alarm.confirm();
        alarmRepository.save(alarm);

        return new StatusResponse(200, "알림 정상적으로 확인되었습니다.");
    }

    // 토큰 조회
    public FCMToken getToken(int userId, Role role) {
        FCMToken fcmTokens = fcmTokenRepository.findByUserIdAndRole(userId, role);;
        if (fcmTokens == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        return fcmTokens;
    }

    // 알림 저장
    private void saveAlarm(String name, String titleId, FCMToken fcmToken, String title, String body) {
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
