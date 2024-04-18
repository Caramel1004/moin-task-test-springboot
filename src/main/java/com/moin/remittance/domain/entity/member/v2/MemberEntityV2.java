package com.moin.remittance.domain.entity.member.v2;

import com.moin.remittance.domain.dto.member.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Component
@Entity
@Table(name = "member_v2")
public class MemberEntityV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INDEX")
    private Long index;

    @Column(name = "USER_ID", unique = true, nullable = false)
    private String userId;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ID_TYPE", nullable = false)
    private String idType;

    @Column(name = "ID_VALUE", nullable = false)
    private String idValue;
}
