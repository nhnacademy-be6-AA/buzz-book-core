INSERT INTO `user` (
    id
) values (
    1
);

INSERT INTO `category` (
    id,
    name
) values (
    1, 'IT'
);

INSERT INTO `delivery_policy` (
    id,
    name,
    standard_price,
    policy_price
) values (
          1, '정책1', 1000, 1000
         );

INSERT INTO `order_status` (
    id, update_date, name
) values (
    1, '2024-01-01T12:00:00Z', '환불'
);

INSERT INTO `wrapping` (
    id, paper, price
) values (
    1, '신문지', 1000
);

INSERT INTO `order` (
    price,
    request,
    address,
    addressDetail,
    zipcode,
    desiredDeliveryDate,
    receiver,
    delivery_policy_id,
    user_id
) VALUES (
             1000,
             '감사합니다.',
             '광주',
             '조선대학교',
             00000,
             '2024-01-01T12:00:00Z',
             'PS',
             (SELECT id FROM delivery_policy WHERE name = '정책1' LIMIT 1),
             (SELECT id FROM user WHERE id = 1 LIMIT 1)
         );

INSERT INTO order_detail (
    price,
    quantity,
    wrap,
    createdDate,
    order_status_id,
    wrapping_id,
    product_id,
    order_id
) VALUES (
             10000,
             1,
             TRUE,
             '2024-01-01T12:00:00Z',
             (SELECT id FROM order_status WHERE name = '환불' LIMIT 1),
             (SELECT id FROM wrapping WHERE paper = '신문지' LIMIT 1),
             (SELECT id FROM product WHERE book_id = 'book1' LIMIT 1),
             (SELECT id FROM `order` WHERE address = '광주' AND addressDetail = '조선대학교' LIMIT 1)
         );

