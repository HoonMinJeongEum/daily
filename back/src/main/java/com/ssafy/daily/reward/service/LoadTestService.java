package com.ssafy.daily.reward.service;

import com.ssafy.daily.common.Content;
import com.ssafy.daily.reward.dto.*;
import com.ssafy.daily.user.entity.Member;
import com.ssafy.daily.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class LoadTestService {
    private final JdbcTemplate jdbc;
    private final ShellService shellService;
    private final MemberRepository memberRepository;

    public List<Long> insertCoupon() {
        final int TOTAL = 20_000;
        final int BATCH = 1000;
        final String sql = """
            INSERT INTO coupon (family_id, description, price, purchased_at, created_at)
            VALUES (?, ?, ?, NULL, NOW())
        """;
        List<Long> ids = new ArrayList<>(TOTAL);

        jdbc.execute((ConnectionCallback<Void>) con -> {
            try (PreparedStatement ps =
                         con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                int inBatch = 0;
                for (int i = 0; i < TOTAL; i++) {
                    ps.setLong(1, 1L);
                    ps.setString(2, "test");
                    ps.setInt(3, 1);
                    ps.addBatch();
                    inBatch++;

                    if (inBatch == BATCH) {
                        ps.executeBatch();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            while (rs.next()) ids.add(rs.getLong(1));
                        }
                        inBatch = 0;
                    }
                }

                if (inBatch > 0) {
                    ps.executeBatch();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        while (rs.next()) ids.add(rs.getLong(1));
                    }
                }
            }
            return null;
        });

        return ids;
    }

    public List<Long> insertBuyCoupon() {
        final int BATCH = 1_000;
        final int total = 20_000;
        final String insertCouponSql = """
            INSERT INTO coupon (family_id, description, price, purchased_at, created_at)
            VALUES (?, ?, ?, NOW(), NOW())
        """;

        final String insertEarnedSql = """
            INSERT INTO earned_coupon (member_id, coupon_id, used_at)
            VALUES (?, ?, NULL)
        """;

        List<Long> couponIds = new ArrayList<>(total);
        List<Long> earnedIds = new ArrayList<>(total);

        jdbc.execute((ConnectionCallback<Void>) con -> {
            try (PreparedStatement ps =
                         con.prepareStatement(insertCouponSql, Statement.RETURN_GENERATED_KEYS)) {
                int inBatch = 0;
                for (int i = 0; i < total; i++) {
                    ps.setLong(1, 1);
                    ps.setString(2, "test");
                    ps.setInt(3, 1);
                    ps.addBatch();
                    if (++inBatch == BATCH) {
                        ps.executeBatch();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            while (rs.next()) couponIds.add(rs.getLong(1));
                        }
                        inBatch = 0;
                    }
                }
                if (inBatch > 0) {
                    ps.executeBatch();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        while (rs.next()) couponIds.add(rs.getLong(1));
                    }
                }
            }

            try (PreparedStatement ps =
                         con.prepareStatement(insertEarnedSql, Statement.RETURN_GENERATED_KEYS)) {
                int inBatch = 0;
                for (Long id : couponIds) {
                    ps.setLong(1, 1);
                    ps.setLong(2, id);
                    ps.addBatch();
                    if (++inBatch == BATCH) {
                        ps.executeBatch();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            while (rs.next()) earnedIds.add(rs.getLong(1));
                        }
                        inBatch = 0;
                    }
                }
                if (inBatch > 0) {
                    ps.executeBatch();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        while (rs.next()) earnedIds.add(rs.getLong(1));
                    }
                }
            }
            return null;
        });

        return earnedIds;
    }
}
