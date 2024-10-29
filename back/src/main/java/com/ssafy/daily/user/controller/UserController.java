package com.ssafy.daily.user.controller;

import com.ssafy.daily.exception.UsernameAlreadyExistsException;
import com.ssafy.daily.user.dto.AddProfileRequest;
import com.ssafy.daily.user.dto.ChoiceMemberRequest;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.dto.JoinRequest;
import com.ssafy.daily.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

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
        // jwt에서 familyId 가져오기
        int familyId = userDetails.getFamily().getId();
        return ResponseEntity.ok(userService.getProfiles(familyId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AddProfileRequest request){

        // jwt에서 familyId 가져오기
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
}
