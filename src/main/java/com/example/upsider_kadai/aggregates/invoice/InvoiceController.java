package com.example.upsider_kadai.aggregates.invoice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> create(@RequestBody @Valid RequestCreateInvoice requset) {
        var invoice = this.invoiceService.create(requset.paymentAmount(), requset.paymentDueDate());
        return ResponseEntity.ok(invoice);
    }
    
    @PostMapping("/invoices/search")
    public ResponseEntity<List<Invoice>> search(@RequestBody @Valid RequestSearchInvoices request) {
        var invoices = this.invoiceService.search(request.userId(), request.paymentDueDateFrom(), request.paymentDueDateTo(), request.pageNumber(), request.pageSize());
        return ResponseEntity.ok(invoices);
    }
}
