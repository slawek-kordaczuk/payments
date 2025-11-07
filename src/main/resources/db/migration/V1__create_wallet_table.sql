CREATE TABLE wallet
(
    id               UUID PRIMARY KEY,
    amount   DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3)     NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_balance CHECK (amount >= 0)
);

CREATE INDEX idx_wallet_currency ON wallet (currency);
