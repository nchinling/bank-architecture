-- Account table. InnoDB + utf8mb4 for MySQL 8. Money as DECIMAL.
CREATE TABLE accounts (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    account_number  VARCHAR(20)   NOT NULL,
    customer_ref    VARCHAR(36)   NOT NULL,
    account_type    VARCHAR(20)   NOT NULL,
    currency        VARCHAR(3)    NOT NULL,
    balance         DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_accounts_number UNIQUE (account_number),
    INDEX idx_accounts_customer (customer_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
