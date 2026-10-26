-- Customer table. InnoDB + utf8mb4 for MySQL 8.
CREATE TABLE customers (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    customer_ref    VARCHAR(36)  NOT NULL,
    full_name       VARCHAR(200) NOT NULL,
    email           VARCHAR(200) NOT NULL,
    national_id     VARCHAR(50)  NULL,
    kyc_status      VARCHAR(20)  NOT NULL,
    kyc_reason      VARCHAR(255) NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_customers_ref   UNIQUE (customer_ref),
    CONSTRAINT uq_customers_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
