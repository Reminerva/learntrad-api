\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_1_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_2_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_3_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_4_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_5_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\COPY m_xauusd(time_bucket_start, open, high, low, closed, volume) FROM '/docker-entrypoint-initdb.d/data/XAUUSD_M1_BATCH_6_OHLCV.csv' WITH (FORMAT csv, HEADER true);
\! touch /tmp/db_initialized.marker