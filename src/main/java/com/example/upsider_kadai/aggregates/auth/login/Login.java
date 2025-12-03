package com.example.upsider_kadai.aggregates.auth.login;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** ログイン履歴 */
@Table("logins")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Login {
    
    @Id
    private final Integer id;

    @Column("user_id")
    private final Integer userId;

    @Column("auth_result")
    private final AuthResult authResult;

    @Column("created_at")
    private final Instant createdAt;

    public static Login create(Integer userId, AuthResult authResult) {
        return new Login(null, userId, authResult, Instant.now());
    }

    public enum AuthResult {

        SUCCESS,

        FAILURE,

        INTERNAL_ERROR

    }
}
