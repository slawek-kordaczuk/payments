CREATE TABLE client
(
    id         UUID PRIMARY KEY,
    wallet_id  UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet FOREIGN KEY (wallet_id) REFERENCES wallet (id)
);

CREATE INDEX idx_client_wallet ON client (wallet_id);
