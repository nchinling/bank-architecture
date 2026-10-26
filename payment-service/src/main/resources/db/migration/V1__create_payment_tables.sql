-- Payment service owns its own accounts + ledger so a transfer is one atomic
-- local transaction. InnoDB + DECIMAL for correct, transactional money handling.
CREATE TABLE accounts (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    account_number  VARCHAR(20)   NOT NULL,
    customer_ref    VARCHAR(36)   NOT NULL,
    currency        VARCHAR(3)    NOT NULL,
    balance         DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    version         BIGINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uq_pay_accounts_number UNIQUE (account_number),
    INDEX idx_pay_accounts_customer (customer_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ledger_entries (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    transaction_ref  VARCHAR(50)   NOT NULL,
    account_number   VARCHAR(20)   NOT NULL,
    customer_ref     VARCHAR(36)   NOT NULL,
    direction        VARCHAR(6)    NOT NULL,
    amount           DECIMAL(19,2) NOT NULL,
    currency         VARCHAR(3)    NOT NULL,
    description      VARCHAR(255)  NULL,
    counterparty     VARCHAR(20)   NULL,
    booked_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_pay_ledger_account (account_number),
    INDEX idx_pay_ledger_txn (transaction_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
