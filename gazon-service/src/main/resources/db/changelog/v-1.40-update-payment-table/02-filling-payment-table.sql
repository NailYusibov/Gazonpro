INSERT INTO payments (bank_card_id, payment_status, create_date_time, order_id, sum, should_save_card)
VALUES (1, 'NOT_PAID', '2023-01-21 11:01', 1, 1500, FALSE);

INSERT INTO payments (bank_card_id, payment_status, create_date_time, order_id, sum, should_save_card)
VALUES (2, 'PAID', '2023-02-22 12:02', 1, 2500, FALSE);

INSERT INTO payments (bank_card_id, payment_status, create_date_time, order_id, sum, should_save_card)
VALUES (3, 'OVERDUE', '2023-03-23 13:03', 1, 3500, FALSE);