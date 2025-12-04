CREATE TABLE "invoices" (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    issue_date DATE NOT NULL,
    payment_amount DECIMAL(15, 2) NOT NULL,
    fee DECIMAL(15, 2) NOT NULL,
    fee_rate DECIMAL(5, 2) NOT NULL,
    tax_amount DECIMAL(15, 2) NOT NULL,
    tax_rate DECIMAL(5, 2) NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    payment_due_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX invoices_payment_due_date ON invoices (payment_due_date);