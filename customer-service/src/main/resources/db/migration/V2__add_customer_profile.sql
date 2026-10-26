-- Richer customer profile. Additive change: new nullable columns on the existing
-- customers table (safe - never edit an applied migration; always add a new one).
-- One ALTER per column keeps this portable across MySQL 8 and the H2 (MySQL mode)
-- database used by the tests.
ALTER TABLE customers ADD COLUMN phone VARCHAR(30) NULL;
ALTER TABLE customers ADD COLUMN address_line1 VARCHAR(200) NULL;
ALTER TABLE customers ADD COLUMN address_line2 VARCHAR(200) NULL;
ALTER TABLE customers ADD COLUMN city VARCHAR(100) NULL;
ALTER TABLE customers ADD COLUMN postal_code VARCHAR(20) NULL;
ALTER TABLE customers ADD COLUMN country VARCHAR(2) NULL;
ALTER TABLE customers ADD COLUMN date_of_birth DATE NULL;
ALTER TABLE customers ADD COLUMN occupation VARCHAR(100) NULL;
ALTER TABLE customers ADD COLUMN preferred_channel VARCHAR(20) NULL;
