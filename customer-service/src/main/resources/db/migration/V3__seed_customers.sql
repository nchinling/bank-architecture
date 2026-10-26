-- Pre-seeded customers with full profiles so the sandbox is not empty and the
-- customer app has realistic profile data to display.
-- customer_ref values match the Keycloak users and the account-service seed
-- (CUST-ALICE, CUST-BOB). KYC status APPROVED for all seeded customers.
INSERT INTO customers
    (customer_ref, full_name, email, national_id, kyc_status, kyc_reason,
     phone, address_line1, address_line2, city, postal_code, country,
     date_of_birth, occupation, preferred_channel, created_at)
VALUES
    ('CUST-ALICE', 'Alice Tan', 'alice@example.com', 'S1234567A', 'APPROVED', 'Seeded customer',
     '+65 9123 4567', '12 Orchard Road', '#05-01', 'Singapore', '238801', 'SG',
     '1995-04-12', 'Software Engineer', 'MOBILE', CURRENT_TIMESTAMP),
    ('CUST-BOB', 'Bob Lim', 'bob@example.com', 'S7654321B', 'APPROVED', 'Seeded customer',
     '+65 9876 5432', '88 Marina Boulevard', '#21-10', 'Singapore', '018981', 'SG',
     '1988-11-30', 'Teacher', 'WEB', CURRENT_TIMESTAMP),
    ('CUST-CARA', 'Cara Wong', 'cara@example.com', 'S2468135C', 'APPROVED', 'Seeded customer',
     '+65 9222 3344', '5 Raffles Place', NULL, 'Singapore', '048618', 'SG',
     '2000-07-08', 'Student', 'MOBILE', CURRENT_TIMESTAMP),
    ('CUST-DAN', 'Dan Rajan', 'dan@example.com', 'S1357924D', 'APPROVED', 'Seeded customer',
     '+65 9555 6677', '20 Tampines Street 11', '#12-34', 'Singapore', '521201', 'SG',
     '1979-02-21', 'Business Owner', 'BRANCH', CURRENT_TIMESTAMP);
