INSERT INTO `delivery_policy` (
    name,
    standard_price,
    policy_price
) values (
          '정책1', 1000, 1000
);

INSERT INTO `order_status` (
    update_date, name
) values (
    '2024-01-01', '환불'
);

INSERT INTO `wrapping` (
    paper, price
) values (
    '신문지', 1000
);

INSERT INTO `order` (
    price,
    request,
    address,
    address_detail,
    zipcode,
    desired_delivery_date,
    receiver,
    delivery_policy_id,
    user_id
) VALUES (
             1000,
             '감사합니다.',
             '광주',
             '조선대학교',
             00000,
             '2024-01-01',
             'PS',
             (SELECT id FROM delivery_policy WHERE name = '정책1' LIMIT 1),
             (SELECT id FROM user WHERE id = 1 LIMIT 1)
         );

INSERT INTO order_detail (
    price,
    quantity,
    wrap,
    create_date,
    order_status_id,
    wrapping_id,
    product_id,
    order_id
) VALUES (
             10000,
             1,
             TRUE,
             STR_TO_DATE('2024-01-01', '%Y-%m-%d'),
             (SELECT id FROM order_status WHERE name = '환불' LIMIT 1),
             (SELECT id FROM wrapping WHERE paper = '신문지' LIMIT 1),
             (SELECT id FROM product WHERE id = 1 LIMIT 1),
             (SELECT id FROM `order` WHERE address = '광주' AND address_detail = '조선대학교' LIMIT 1)
         ),
         (
             20000,
             1,
             FALSE,
             STR_TO_DATE('2024-01-01', '%Y-%m-%d'),
             (SELECT id FROM order_status WHERE name = '환불' LIMIT 1),
             (SELECT id FROM wrapping WHERE paper = '신문지' LIMIT 1),
             (SELECT id FROM product WHERE id = 1 LIMIT 1),
             (SELECT id FROM `order` WHERE address = '광주' AND address_detail = '조선대학교' LIMIT 1)
         );
