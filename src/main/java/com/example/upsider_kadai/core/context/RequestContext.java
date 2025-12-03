package com.example.upsider_kadai.core.context;

public record RequestContext(
    String companyName,
    String name,
    String email
) {
    public static final ScopedValue<RequestContext> SCOPED_VALUE = ScopedValue.newInstance();
}
