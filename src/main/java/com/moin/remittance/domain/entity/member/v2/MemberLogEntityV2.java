package com.moin.remittance.domain.entity.member.v2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "member_log_v2")
public class MemberLogEntityV2 {
    @Id
    @Column(name = "userId", nullable = false)
    private String userId;
    @Column(name = "token", unique = true, nullable = false)
    private String token;
}
