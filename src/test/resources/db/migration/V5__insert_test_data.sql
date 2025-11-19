-- File: src/test/resources/db/migration/V5__insert_test_data.sql
-- Test data only for integration tests (placed in src/test/resources)

-- Insert wallets
INSERT INTO wallet (id, amount, currency, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 500.00, 'PLN', now(), now()),
       ('22222222-2222-2222-2222-222222222222', 2500.00, 'EUR', now(), now()),
       ('33333333-3333-3333-3333-333333333333', 750.00, 'USD', now(), now())
    ON CONFLICT (id) DO NOTHING;

-- Insert clients (referencing wallets)
INSERT INTO client (id, wallet_id, created_at, updated_at)
VALUES ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', now(), now()),
       ('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', now(), now()),
       ('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', now(), now())
    ON CONFLICT (id) DO NOTHING;

-- Insert payments (referencing clients)
INSERT INTO payment (id, client_id, order_id, amount, currency, status, created_at, updated_at)
VALUES ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 500.00, 'PLN', 'SUCCESS', now(), now()),
       ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 666.00, 'EUR', 'CANCELLED', now(), now())
    ON CONFLICT (id) DO NOTHING;