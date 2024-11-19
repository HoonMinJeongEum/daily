package com.ssafy.daily.user.controller;

import com.ssafy.daily.user.dto.*;
import com.ssafy.daily.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
        userService.login(request, response);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        userService.logout(request, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkExist(@PathVariable String username){
        userService.checkExist(username);
        return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequest request){
        userService.join(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfiles(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getProfiles(userDetails));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProfile(@AuthenticationPrincipal CustomUserDetails userDetails
        , @RequestParam("file") MultipartFile file,
          @RequestParam("memberName") String memberName){
        userService.addProfile(userDetails, file, memberName);
        return ResponseEntity.ok("프로필 등록 성공");
    }
    @PostMapping("/member")
    public ResponseEntity<?> choiceMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody ChoiceMemberRequest request, HttpServletResponse response){
        int memberId = request.getMemberId();
        String jwt = userService.choiceMember(userDetails, request, response);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .body("프로필 선택: " + memberId);
    }

    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable int memberId){
        userService.deleteMember(memberId);
        return ResponseEntity.ok("성공적으로 프로필이 삭제되었습니다.");
    }

    @PatchMapping("/member")
    public ResponseEntity<?> modifyMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody ModifyNameRequest request){
        userService.modifyMemberName(request, userDetails);
        return ResponseEntity.ok("프로필 이름이 정상적으로 수정되었습니다.");
    }

    @PatchMapping("/member/img")
    public ResponseEntity<?> modifyMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestParam("file") MultipartFile file){
        userService.modifyProfileImg(file, userDetails);
        return ResponseEntity.ok("프로필 사진이 정상적으로 수정되었습니다.");
    }

    @GetMapping("/main")
    public ResponseEntity<?> getMainProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getMainProfile(userDetails));
    }

}
