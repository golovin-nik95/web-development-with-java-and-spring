MERGE INTO orders (id, date, total, status, user_id) KEY (id) VALUES
(1, '2019-10-14', 93.40, 'PENDING', 1),
(2, '2019-10-14', 213.20, 'PENDING', 1),
(3, '2019-10-14', 437.70, 'PENDING', 1);

MERGE INTO order_items (id, ordinal, product_id, quantity, subtotal, order_id) KEY (id) VALUES
(1, 1, 1, 7, 53.20, 1),
(2, 2, 2, 2, 25.00, 1),
(3, 3, 3, 4, 15.20, 1),
(4, 1, 2, 2, 25.00, 2),
(5, 2, 3, 4, 15.20, 2),
(6, 3, 4, 1, 173.00, 2),
(7, 1, 3, 4, 15.20, 3),
(8, 2, 4, 1, 173.00, 3),
(9, 3, 5, 5, 249.50, 3);