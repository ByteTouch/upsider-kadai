package com.example.upsider_kadai.aggregates.auth.user;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record RequestRegister(

    @NotBlank
    @Length(max = 200)
    String companyName,

    @NotBlank
    @Length(max = 200)
    String name,

    @NotBlank
    @Length(max = 200)
    String email,

    @NotEmpty
    @Length(max = 200)
    String password

) {}
