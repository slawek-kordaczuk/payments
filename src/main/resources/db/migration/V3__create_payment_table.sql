CREATE TABLE payment
(
    id         UUID PRIMARY KEY,
    client_id  UUID           NOT NULL,
    order_id   UUID           NOT NULL,
    amount     DECIMAL(19, 4) NOT NULL,
    currency   VARCHAR(3)     NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client (id),
    CONSTRAINT valid_status CHECK (status IN ('NEW', 'SUCCESS', 'CANCELLED', 'FAILED'))
);

CREATE INDEX idx_payment_client ON payment (client_id);
CREATE INDEX idx_payment_status ON payment (status);
CREATE INDEX idx_payment_created ON payment (created_at);
