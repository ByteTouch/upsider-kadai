package com.example.upsider_kadai.aggregates.auth.login;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RequestLogin(

    @NotBlank
    @Length(max = 200)
    String email,

    @NotEmpty
    @Length(max = 200)
    String password

) {}
