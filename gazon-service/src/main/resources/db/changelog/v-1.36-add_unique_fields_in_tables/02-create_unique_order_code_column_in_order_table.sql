ALTER TABLE orders
    ADD CONSTRAINT unique_order_code UNIQUE (order_code);