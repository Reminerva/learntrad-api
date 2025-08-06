CREATE TABLE IF NOT EXISTS t_quiz (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36),
    time_bucket_start TIMESTAMPTZ NOT NULL,
    n_size CHAR(10) NOT NULL,
    time_frame CHAR(20) NOT NULL,
    market_data_type CHAR(10) NOT NULL,
    vertical_translation NUMERIC NOT NULL,
    scale NUMERIC NOT NULL,
    result NUMERIC,
    price_at NUMERIC,
    take_profit_at NUMERIC,
    stop_loss_at NUMERIC,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6)
);