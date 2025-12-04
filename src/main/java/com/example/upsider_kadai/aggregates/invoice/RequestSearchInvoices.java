package com.example.upsider_kadai.aggregates.invoice;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RequestSearchInvoices(

    @NotNull
    Integer userId,

    @JsonFormat(pattern = "yyyy/MM/dd")
    @NotNull
    LocalDate paymentDueDateFrom,

    @JsonFormat(pattern = "yyyy/MM/dd")
    @NotNull
    LocalDate paymentDueDateTo,

    @NotNull
    @PositiveOrZero
    Integer pageNumber,

    @NotNull
    @Range(min = 1, max = 30)
    Integer pageSize
) {}
