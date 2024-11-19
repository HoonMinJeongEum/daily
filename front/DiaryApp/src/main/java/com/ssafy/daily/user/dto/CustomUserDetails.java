package com.ssafy.daily.user.dto;

import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Family family;
    private final Member member;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
//                return family.getRole();
                return "ROLE";
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return family.getPassword();
    }

    @Override
    public String getUsername() {
        return family.getUsername();
    }

    public int getFamilyId(){
        return family.getId();
    }

    public int getMemberId(){
        return member != null ? member.getId() : 0;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
