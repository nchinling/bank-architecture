-- Append-only ledger. Each row is a single leg (DEBIT or CREDIT) against one
-- account. The two legs of a transfer share the same transaction_ref.
-- Rows are immutable: never UPDATE or DELETE - only INSERT.
CREATE TABLE ledger_entries (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    transaction_ref  VARCHAR(36)   NOT NULL,
    account_number   VARCHAR(20)   NOT NULL,
    customer_ref     VARCHAR(36)   NOT NULL,
    direction        VARCHAR(6)    NOT NULL,   -- DEBIT or CREDIT
    amount           DECIMAL(19,2) NOT NULL,
    currency         VARCHAR(3)    NOT NULL,
    description      VARCHAR(255)  NULL,
    counterparty     VARCHAR(20)   NULL,       -- the other account in the transfer
    booked_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_ledger_account (account_number),
    INDEX idx_ledger_customer (customer_ref),
    INDEX idx_ledger_txn (transaction_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
