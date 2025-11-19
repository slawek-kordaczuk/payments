-- Insert default wallet
INSERT INTO wallet (id, amount, currency, created_at, updated_at)
VALUES ('99999999-9999-9999-9999-999999999999', 1000.00, 'PLN', now(), now())
    ON CONFLICT (id) DO NOTHING;

-- Insert default client
INSERT INTO client (id, wallet_id, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', '99999999-9999-9999-9999-999999999999', now(), now())
    ON CONFLICT (id) DO NOTHING;
