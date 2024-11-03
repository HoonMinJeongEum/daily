package com.ssafy.daily.user.controller;

import com.ssafy.daily.exception.UsernameAlreadyExistsException;
import com.ssafy.daily.user.dto.*;
import com.ssafy.daily.user.jwt.JWTUtil;
import com.ssafy.daily.user.repository.RefreshRepository;
import com.ssafy.daily.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Iterator;

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
        int familyId = userDetails.getFamily().getId();
        return ResponseEntity.ok(userService.getProfiles(familyId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AddProfileRequest request){
        int familyId = userDetails.getFamily().getId();

        userService.addProfile(familyId, request);
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

    @GetMapping("/main")
    public ResponseEntity<?> getMainProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(userService.getMainProfile(userDetails));
    }

}
