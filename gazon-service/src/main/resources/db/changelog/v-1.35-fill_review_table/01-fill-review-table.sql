INSERT INTO review
(product_id, pros, cons, "comment", rating, helpful_counter, not_helpful_counter, entity_status)
VALUES (4, 'good', 'порвана упаковка', 'good Hugg', 5, 3, 1, 'ACTIVE'),
       (4, 'amazing', 'bed delivery', ' no good Hugg', 3, 5, 9, 'ACTIVE'),
       (4, 'not bad', 'bad box', 'middle Hugg', 2, NULL, NULL, 'ACTIVE'),
       (4, 'not bad', 'bad box', 'middle Hugg', 4, 7, NULL, 'ACTIVE'),
       (4, 'good', 'not good', 'awesome Hugg', 3, NULL, 4, 'ACTIVE');