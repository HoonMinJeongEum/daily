package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.reward.dto.BuyCouponRequest;
import com.ssafy.daily.reward.entity.Coupon;
import com.ssafy.daily.reward.repository.CouponRepository;
import com.ssafy.daily.user.dto.CustomUserDetails;
import com.ssafy.daily.user.entity.Family;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.FamilyRepository;
import com.ssafy.daily.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private ShellService shellService;

    private CustomUserDetails mockUser;
    private Coupon coupon;
    private Member member;
    private List<Member> memberList;
    private List<CustomUserDetails> userList;
    private List<Exception> exceptions;
    private final int threadCount = 4;

    @BeforeEach
    void setUp() {
        memberList = new ArrayList<>();
        userList = new ArrayList<>();
        exceptions = Collections.synchronizedList(new ArrayList<>());

        Family family = Family.builder()
                .username("test1234")
                .password("test1234")
                .build();
        familyRepository.save(family);

        member = Member.builder()
                .family(family)
                .name("테스트 사용자")
                .img(null)
                .build();
        memberRepository.save(member);

        coupon = Coupon.builder()
                .family(family)
                .description("테스트 쿠폰")
                .price(10)
                .createdAt(LocalDateTime.now())
                .build();
        couponRepository.save(coupon);

//        for (int i = 0; i < threadCount; i++) {
//            Member newMember = Member.builder()
//                    .family(member.getFamily())
//                    .name("테스트 사용자 " + i)
//                    .img(null)
//                    .build();
//            memberList.add(memberRepository.save(newMember));
//            shellService.saveShellLog(newMember, 100, Content.MISSION);
//            CustomUserDetails newMockUser = mock(CustomUserDetails.class);
//            when(newMockUser.getMember()).thenReturn(newMember);
//            userList.add(newMockUser);
//        }
        shellService.saveShellLog(member, 100, Content.MISSION);
        mockUser = mock(CustomUserDetails.class);
        when(mockUser.getMember()).thenReturn(member);
    }

    /**
     * [단일 사용자] 쿠폰을 정상적으로 구매할 수 있는지 테스트
     */
    @Test
    void testBuyCouponSuccessfully() throws Exception {
        // given: 구매 요청 객체 생성
        BuyCouponRequest request = new BuyCouponRequest();
        request.setCouponId(coupon.getId());

        // when: 쿠폰 구매 실행
        int remainingShells = couponService.buyCoupon(mockUser, request);

        // then: 구매된 쿠폰의 purchasedAt 값이 설정되었는지 확인
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertNotNull(updatedCoupon.getPurchasedAt()); // 구매 날짜가 설정되어야 함
        assertTrue(remainingShells >= 0); // 남은 조개 개수가 0 이상인지 확인
    }

    /**
     * [예외 테스트] 이미 구매한 쿠폰을 다시 구매할 경우 예외가 발생하는지 테스트
     */
    @Test
    void testBuyCoupon_AlreadyOwnedException() {
        // given: 이미 구매한 상태로 변경
        coupon.buy(LocalDateTime.now());

        BuyCouponRequest request = new BuyCouponRequest();
        request.setCouponId(coupon.getId());

        // when & then: 이미 구매한 쿠폰을 다시 구매하면 예외 발생
        assertThrows(Exception.class, () -> couponService.buyCoupon(mockUser, request));
    }

    /**
     * [동시성 테스트] 여러 사용자가 동시에 쿠폰을 구매할 때 동시성 문제가 발생하지 않는지 검증
     */
    @Test
    void testConcurrentBuyCoupon_WithMultipleUsers() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    BuyCouponRequest request = new BuyCouponRequest();
                    request.setCouponId(coupon.getId());

                    Member newMember = memberRepository.findById(finalI + 1)
                            .orElseThrow(() -> new EmptyResultDataAccessException("해당 프로필을 찾을 수 없습니다.", 1));
                    CustomUserDetails newMockUser = mock(CustomUserDetails.class);
                    when(newMockUser.getMember()).thenReturn(newMember);
                    couponService.buyCoupon(newMockUser, request);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        if (!exceptions.isEmpty()) {
            System.out.println("❌ 발생한 예외 목록:");
            for (Exception e : exceptions) {
                e.printStackTrace();
            }
        }

        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertNotNull(updatedCoupon.getPurchasedAt());
    }
}
