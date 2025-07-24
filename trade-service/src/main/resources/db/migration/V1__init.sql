CREATE TABLE t_trade (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    lot NUMERIC NOT NULL,
    price_at NUMERIC NOT NULL,
    stop_loss_at NUMERIC NOT NULL,
    take_profit_at NUMERIC NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    trade_at TIMESTAMPTZ,
    market_data_type VARCHAR(20) NOT NULL,
    trade_status VARCHAR(20) NOT NULL,
    trade_type VARCHAR(20) NOT NULL,
    closed_at NUMERIC,
    expired_at TIMESTAMP
);