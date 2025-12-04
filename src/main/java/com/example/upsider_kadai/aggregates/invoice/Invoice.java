package com.example.upsider_kadai.aggregates.invoice;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.example.upsider_kadai.core.util.InstantUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Table("invoices")
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__({@JsonCreator}))
@Getter
public class Invoice {
    
    @Id
    private final Integer id;

    @Column("user_id")
    private final Integer userId;

    /** 発行日 */
    @Column("issue_date")
    private final LocalDate issueDate;

    /** 支払金額 */
    @Column("payment_amount")
    private final BigDecimal paymentAmount;

    /** 手数料 */
    private final BigDecimal fee;

    /** 手数料率 */
    @Column("fee_rate")
    private final BigDecimal feeRate;

    /** 消費税 */
    @Column("tax_amount")
    private final BigDecimal taxAmount;

    /** 消費税率 */
    @Column("tax_rate")
    private final BigDecimal taxRate;

    /** 請求金額 */
    @Column("total_amount")
    private final BigDecimal totalAmount;

    /** 支払期日 */
    @Column("payment_due_date")
    private final LocalDate paymentDueDate;

    @Column("created_at")
    private final Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    public static Invoice create(Integer userId, LocalDate issueDate, BigDecimal paymentAmount, BigDecimal fee,
            BigDecimal feeRate, BigDecimal taxAmount, BigDecimal taxRate, BigDecimal totalAmount, LocalDate paymentDueDate) {
        var now = InstantUtils.millisNow();
        return new Invoice(null, userId, issueDate, paymentAmount, fee, feeRate, taxAmount, taxRate, totalAmount, paymentDueDate, now, now);
    }
}
