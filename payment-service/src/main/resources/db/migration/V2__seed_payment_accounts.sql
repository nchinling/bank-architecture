-- Seeded identically to account-service so transfers work out of the box.
INSERT INTO accounts (account_number, customer_ref, currency, balance, status) VALUES
    ('ACC-1001', 'CUST-ALICE', 'SGD', 5000.00, 'ACTIVE'),
    ('ACC-1002', 'CUST-ALICE', 'SGD', 1200.50, 'ACTIVE'),
    ('ACC-2001', 'CUST-BOB',   'SGD', 8300.75, 'ACTIVE'),
    ('ACC-2002', 'CUST-BOB',   'SGD',  450.00, 'ACTIVE');
