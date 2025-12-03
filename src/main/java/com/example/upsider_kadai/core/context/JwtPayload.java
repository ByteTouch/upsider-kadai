package com.example.upsider_kadai.core.context;

public record JwtPayload(
    String companyName,
    String name,
    String email
) {}
