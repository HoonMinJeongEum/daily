package com.ssafy.daily.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Member가 여러 Shell을 가질 수 있는 관계 설정
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Shell> shellLog;  // Shell과의 일대다 관계
}
