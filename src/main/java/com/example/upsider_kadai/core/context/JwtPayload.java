package com.example.upsider_kadai.core.context;

public record JwtPayload(
    int userId,
    String companyName,
    String name,
    String email
) {}
