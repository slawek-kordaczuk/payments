-- File: src/test/resources/db/migration/V4__insert_test_data.sql
-- Test data only for integration tests (placed in src/test/resources)

-- Insert wallets
INSERT INTO wallet (id, amount, currency, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 1000.00, 'PLN', now(), now()) ON CONFLICT (id) DO NOTHING;

-- Insert clients (referencing wallet)
INSERT INTO client (id, wallet_id, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', now(),
        now()) ON CONFLICT (id) DO NOTHING;

-- Insert payments (referencing client)
INSERT INTO payment (id, client_id, order_id, status, created_at, updated_at)
VALUES ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'CANCELLED', now(), now()),
       ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'SUCCESS', now(), now()) ON CONFLICT (id) DO NOTHING;
