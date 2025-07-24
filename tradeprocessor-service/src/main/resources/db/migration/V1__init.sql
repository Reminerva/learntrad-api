CREATE TABLE m_trade_processed (
    id VARCHAR(36) PRIMARY KEY,
    price_at NUMERIC NOT NULL,
    stop_loss_at NUMERIC NOT NULL,
    take_profit_at NUMERIC NOT NULL,
    market_data_type VARCHAR(20) NOT NULL,
    trade_status VARCHAR(20) NOT NULL,
    trade_type VARCHAR(20) NOT NULL,
    closed_at NUMERIC,
    expired_at TIMESTAMP,
    user_id VARCHAR(36) NOT NULL
);
CREATE TABLE m_trade_processed_user (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    customer_fullname VARCHAR(100)
);
ALTER TABLE m_trade_processed ADD CONSTRAINT fk_trade_processed_user FOREIGN KEY (user_id) REFERENCES m_trade_processed_user (user_id);