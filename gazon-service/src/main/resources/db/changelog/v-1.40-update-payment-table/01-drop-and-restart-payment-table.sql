DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          bank_card_id BIGINT REFERENCES bank_card (id) NOT NULL,
                          payment_status VARCHAR(255) NOT NULL,
                          create_date_time TIMESTAMP NOT NULL,
                          order_id BIGINT REFERENCES orders (id) NOT NULL,
                          sum DECIMAL NOT NULL,
                          should_save_card BOOLEAN NOT NULL DEFAULT FALSE
);