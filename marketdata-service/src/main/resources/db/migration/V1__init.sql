CREATE TABLE IF NOT EXISTS m_xauusd (
    time_bucket_start TIMESTAMPTZ PRIMARY KEY,
    open NUMERIC NOT NULL,
    high NUMERIC NOT NULL,
    low NUMERIC NOT NULL,
    closed NUMERIC NOT NULL,
    volume BIGINT NOT NULL DEFAULT 0
);
CREATE EXTENSION IF NOT EXISTS timescaledb;
SELECT create_hypertable('m_xauusd', 'time_bucket_start', if_not_exists => TRUE);
