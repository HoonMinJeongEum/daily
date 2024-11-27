# 어린이 정서발달을 위한 소통형 그림일기 앱

feat. Samsung Galaxy Tab, Spen

---

<div align="center">
<h2>🎨 어린이 정서발달을 위한 소통형 그림일기 앱 🌟</h2>
<b>"아이와의 소통, 그림을 통해 더욱 풍성해집니다."</b>
<br>
<b>"태블릿과 그림으로 아이의 마음을 함께 나눠요."</b>
<br><br>
<strong>어린이와 부모가 함께 만드는 그림일기!<br>정서적 안정과 상호작용을 위한 최적의 도구입니다.</strong>
<br><br> 
</div>

---

## 📌 목표

**어린이들이 필요로 하는 감정적, 사회적 지원을 다양한 컨텐츠와 부모님과의 소통을 통해 제공하여 정서적 안정을 도모합니다.**

---

## 💡 주요 기능

### **그림 일기**

- **기능**  
  - 그림 그리기 및 일기 작성  
  - 작성한 그림일기의 저장 및 전송 (부모 알림 연동)  
  - 일기의 감정에 어울리는 배경음악(BGM) 생성 (AI 활용)  
  - 그림 그리는 과정 애니메이션화 기능  

### **그림 퀴즈**

- **기능**  
  - 부모와 아이 간 실시간 그림 맞추기 퀴즈  
  - 실시간 화상 통화 및 상호작용 지원  
  - 아이가 학습한 단어 기반 제시어 추천  

### **단어 학습**

- **기능**  
  - 단어 따라 쓰기  
  - 단어와 관련된 그림 연계로 학습 효과 증진  

---

## 🎯 부가 기능

### **조개 및 상점 시스템**

- 활동 참여 보상으로 조개 지급  
- 조개로 부모가 등록한 "소원 쿠폰" 또는 그림일기에 사용할 "스티커" 구매 가능  

---

## 🚀 기술적 도전

### **WebRTC를 이용한 P2P 통신**  
- 그림 퀴즈에서 화상 통화 기능 구현

### **Node.js와 Socket.io를 이용한 실시간 통신**  
- 그림 퀴즈에서 양방향 실시간 데이터 전송 및 동기화

### **CLOVA OCR + GPT4.0 + Meta MusicGen AI 파이프라인**  
- 그림일기 저장 시 BGM 생성
- 어린이의 그림 및 텍스트 데이터를 분석해 감정과 어울리는 음악 제공

---

## 기술 스택

<table>
  <tr>
    <th>구분</th>
    <th>기술 스택</th>
  </tr>
  <tr>
    <td align="center"><b>Frontend</b></td>
    <td>
      <img src="https://img.shields.io/badge/Kotlin-%230095D5.svg?style=flat&logo=kotlin&logoColor=white" />
      <img src="https://img.shields.io/badge/Jetpack%20Compose-%232B272E.svg?style=flat&logo=android&logoColor=white" />
    </td>
  </tr>
  <tr>
    <td align="center"><b>Backend</b></td>
    <td>
      <img src="https://img.shields.io/badge/Spring%20Boot-%236DB33F.svg?style=flat&logo=springboot&logoColor=white" />
      <img src="https://img.shields.io/badge/Node.js-339933.svg?style=flat&logo=node.js&logoColor=white" />
      <img src="https://img.shields.io/badge/WebRTC-%23FF5722.svg?style=flat&logo=webrtc&logoColor=white" />
      <img src="https://img.shields.io/badge/WebSocket-%231E90FF.svg?style=flat&logo=websocket&logoColor=white" />
      <img src="https://img.shields.io/badge/GPT4-%237A0C00.svg?style=flat&logo=openai&logoColor=white" />
    </td>
  </tr>
  <tr>
    <td align="center"><b>DB</b></td>
    <td>
      <img src="https://img.shields.io/badge/MySQL-%2300758f.svg?style=flat&logo=mysql&logoColor=white" />
    </td>
  </tr>
  <tr>
    <td align="center"><b>CI/CD</b></td>
    <td>
      <img src="https://img.shields.io/badge/Docker-%232496ED.svg?style=flat&logo=docker&logoColor=white" />
      <img src="https://img.shields.io/badge/NGINX-%23009639.svg?style=flat&logo=nginx&logoColor=white" />
      <img src="https://img.shields.io/badge/AWS-%23FF9900.svg?style=flat&logo=amazon-aws&logoColor=white" />
    </td>
  </tr>
  <tr>
    <td align="center"><b>Cooperation</b></td>
    <td>
      <img src="https://img.shields.io/badge/GitLab-%23FCA121.svg?style=flat&logo=gitlab&logoColor=white" />
      <img src="https://img.shields.io/badge/jirasoftware-0052CC?style=flat&logo=jirasoftware&logoColor=white" />
      <img src="https://img.shields.io/badge/notion-000000?style=flat&logo=notion&logoColor=white" />
    </td>
  </tr>
</table>

---

## 👥 팀원 소개 및 역할

| 이름     | 역할 및 기여                                           |
| -------- | ------------------------------------------------------ |
| 권정솔   | 팀장, 백엔드/디자인 팀장, 프론트엔드 보조, JWT 및 SpringSecurity를 이용한 로그인 구현, UI/UX 설계       |
| 김동환   | 인프라, 프론트엔드 보조, 아키텍처 설계 및 DevOps 구축, 부모용 앱 프레임 설계    |
| 김태훈   | 백엔드/프론트엔드, Socket.io 및 OpenVidu를 이용한 실시간 그림퀴즈 구현          |
| 이권민   | 프론트엔드 팀장, 단어 학습 및 메인 화면 구현              |
| 이승지   | AI/백엔드, GPT 및 MusicGen 통합하여 BGM 생성 AI 파이프라인 구현          |
| 정훈     | 프론트엔드, 그림일기 구현           |