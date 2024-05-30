ALTER TABLE passport
    ADD CONSTRAINT unique_passport_number UNIQUE (passport_number);