package com.example.upsider_kadai.aggregates.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.upsider_kadai.core.context.RequestContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    // Optional: these constants can also be set as properties (environment arguments).
    private static final int amountScale = 2;

    private static final int amountPrecision = 15;

    private static final int feeTaxPrecision = 5;

    private final InvoiceRepository invoiceRepository;

    private final InvoiceProperties invoiceProperties;

    @Transactional
    public Invoice create(BigDecimal paymentAmount, LocalDate paymentDueDate) {
        // Option: In real application, we should first check that if the user is enabled for this operation.

        if (paymentDueDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "'paymentDueDate' must not be before today.");
        }

        try {
            paymentAmount = paymentAmount.setScale(amountScale);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, String.format("The scale of 'paymentAmount' must not be greater than %d", amountScale));
        }
        if (paymentAmount.precision() > amountPrecision) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, String.format("The precision of 'paymentAmount' must not be greater than %d", amountPrecision));
        }

        BigDecimal feeRate = BigDecimal.valueOf(this.invoiceProperties.feeRate());
        BigDecimal taxRate = BigDecimal.valueOf(this.invoiceProperties.taxRate());
        int userId = RequestContext.SCOPED_VALUE.get().userId();

        BigDecimal fee = paymentAmount.multiply(feeRate.divide(ONE_HUNDRED)).setScale(amountScale, RoundingMode.HALF_EVEN);
        BigDecimal taxAmount = fee.multiply(taxRate.divide(ONE_HUNDRED)).setScale(amountScale, RoundingMode.HALF_EVEN);
        if (fee.precision() > feeTaxPrecision || taxAmount.precision() > feeTaxPrecision) {
            var msg = String.format("The precision of calculated 'fee' or 'taxAmount' is greater than %d", feeTaxPrecision);
            log.error(msg);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, msg);
        }

        BigDecimal totalAmount = paymentAmount.add(fee).add(taxAmount);

        var invoice = Invoice.create(userId, LocalDate.now(), paymentAmount, fee, feeRate, taxAmount, taxRate, totalAmount, paymentDueDate);
        return this.invoiceRepository.save(invoice);
    }

    public List<Invoice> search(Integer userId, LocalDate paymentDueDateFrom, LocalDate paymentDueDateTo, int pageNumber, int pageSize) {
        // Option: In real application, we may allow multiple roles to do this operation.
        if (! Objects.equals(userId, RequestContext.SCOPED_VALUE.get().userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (paymentDueDateFrom.isAfter(paymentDueDateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'paymentDueDateFrom' must be before than 'paymentDueDateTo'.");
        }

        return this.invoiceRepository.findByUserIdAndPaymentDueDateBetweenOrderByPaymentDueDate(userId, paymentDueDateFrom, paymentDueDateTo ,PageRequest.of(pageNumber, pageSize));
    }
}
