package com.example.upsider_kadai.aggregates.invoice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@ConfigurationProperties(prefix = "aggregates.invoice")
@Validated
public record InvoiceProperties(

    @NotNull
    @PositiveOrZero
    Double feeRate,

    @NotNull
    @PositiveOrZero
    Double taxRate

) {}
