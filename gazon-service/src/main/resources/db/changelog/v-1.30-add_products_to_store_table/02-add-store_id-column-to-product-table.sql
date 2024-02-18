ALTER TABLE public.product
    ADD CONSTRAINT product___fk
        FOREIGN KEY (store_id) REFERENCES store;
