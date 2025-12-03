package com.example.upsider_kadai.aggregates.auth.user;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Table("users")
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@JsonCreator}))
@Getter
public class User {

    @Id
    private final Integer id;

    @Column("company_name")
    private final String companyName;

    private final String name;

    private final String email;

    @Column("encoded_password")
    private final String encodedPassword;

    @Column("created_at")
    private final Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    public static User create(String companyName, String name, String email, String hashedPassword) {
        var now = Instant.now();
        return new User(null, companyName, name, email, hashedPassword, now, now);
    }
}
