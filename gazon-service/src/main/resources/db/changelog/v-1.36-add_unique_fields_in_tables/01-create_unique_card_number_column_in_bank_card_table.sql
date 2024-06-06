ALTER TABLE bank_card
    ADD CONSTRAINT unique_card_number UNIQUE (card_number);