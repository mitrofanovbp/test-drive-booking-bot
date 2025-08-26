-- Flyway V1: create base schema, constraints, indexes
-- All timestamps are stored as TIMESTAMPTZ (UTC).
CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL PRIMARY KEY,
    telegram_id  BIGINT NOT NULL UNIQUE,
    username     TEXT,
    name         TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_telegram_id ON users (telegram_id);

CREATE TABLE IF NOT EXISTS cars (
    id           BIGSERIAL PRIMARY KEY,
    model        TEXT NOT NULL,
    description  TEXT
);

CREATE TABLE IF NOT EXISTS bookings (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    car_id     BIGINT NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    datetime   TIMESTAMPTZ NOT NULL,
    status     TEXT NOT NULL,
    CONSTRAINT chk_booking_status CHECK (status IN ('CONFIRMED','CANCELED'))
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings (user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_car_id ON bookings (car_id);
CREATE INDEX IF NOT EXISTS idx_bookings_datetime ON bookings (datetime);

-- Protect against double-booking of confirmed slots for the same car
CREATE UNIQUE INDEX IF NOT EXISTS uq_booking_car_slot_confirmed
    ON bookings (car_id, datetime)
    WHERE status = 'CONFIRMED';
