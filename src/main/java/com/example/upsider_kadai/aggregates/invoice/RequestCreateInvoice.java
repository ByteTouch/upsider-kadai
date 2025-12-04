package com.example.upsider_kadai.aggregates.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestCreateInvoice(

    @Positive
    @NotNull
    BigDecimal paymentAmount,

    @JsonFormat(pattern = "yyyy/MM/dd")
    @NotNull
    LocalDate paymentDueDate

) {}
