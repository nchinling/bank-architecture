-- Pre-seeded synthetic accounts so the sandbox is never empty.
-- customer_ref values match the Keycloak test users (CUST-ALICE, CUST-BOB).
INSERT INTO accounts (account_number, customer_ref, account_type, currency, balance, status) VALUES
    ('ACC-1001', 'CUST-ALICE', 'SAVINGS',  'SGD', 5000.00, 'ACTIVE'),
    ('ACC-1002', 'CUST-ALICE', 'CURRENT',  'SGD', 1200.50, 'ACTIVE'),
    ('ACC-2001', 'CUST-BOB',   'SAVINGS',  'SGD', 8300.75, 'ACTIVE'),
    ('ACC-2002', 'CUST-BOB',   'CURRENT',  'SGD',  450.00, 'ACTIVE');
