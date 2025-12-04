package com.example.upsider_kadai.aggregates.invoice;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    List<Invoice> findByUserIdAndPaymentDueDateBetweenOrderByPaymentDueDate(Integer userId, LocalDate from, LocalDate to, Pageable pageable);

}
