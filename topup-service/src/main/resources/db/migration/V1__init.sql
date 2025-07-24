CREATE TABLE t_top_up (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    payment_type VARCHAR(255) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);