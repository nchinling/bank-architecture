-- Seed historical transactions so statements are not empty on first run.
-- Accounts/customers match the account-service and Keycloak seed data.

-- A past transfer: Bob (ACC-2001) -> Alice (ACC-1001), 150.00 SGD
INSERT INTO ledger_entries
    (transaction_ref, account_number, customer_ref, direction, amount, currency, description, counterparty, booked_at) VALUES
    ('TXN-SEED-0001', 'ACC-2001', 'CUST-BOB',   'DEBIT',  150.00, 'SGD', 'Transfer to Alice',   'ACC-1001', '2026-09-01 10:00:00'),
    ('TXN-SEED-0001', 'ACC-1001', 'CUST-ALICE', 'CREDIT', 150.00, 'SGD', 'Transfer from Bob',   'ACC-2001', '2026-09-01 10:00:00');

-- A salary credit into Alice's savings
INSERT INTO ledger_entries
    (transaction_ref, account_number, customer_ref, direction, amount, currency, description, counterparty, booked_at) VALUES
    ('TXN-SEED-0002', 'ACC-1001', 'CUST-ALICE', 'CREDIT', 3200.00, 'SGD', 'Monthly salary', NULL, '2026-09-25 09:00:00');

-- A bill payment debit from Bob's current account
INSERT INTO ledger_entries
    (transaction_ref, account_number, customer_ref, direction, amount, currency, description, counterparty, booked_at) VALUES
    ('TXN-SEED-0003', 'ACC-2002', 'CUST-BOB', 'DEBIT', 75.50, 'SGD', 'Utility bill', NULL, '2026-09-28 14:30:00');
