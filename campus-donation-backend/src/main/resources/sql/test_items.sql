-- 旧物记录测试数据（100条）
-- 状态：0-待整理 1-已入库 2-已对接 3-已回收

-- 为已接收的预约创建旧物记录（ID 6-13，共8条预约）
-- 预约6：书籍25本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(6, 'book', 10, 'good', '小说类', NULL, 1, 'A区-1号架', 0, '2026-02-10 11:00:00', '2026-02-10 11:00:00'),
(6, 'book', 15, 'good', '杂志类', NULL, 1, 'A区-2号架', 0, '2026-02-10 11:00:00', '2026-02-10 11:00:00');

-- 预约7：衣物12件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(7, 'clothing', 12, 'good', '春季外套和T恤', NULL, 1, 'B区-1号柜', 0, '2026-02-10 15:00:00', '2026-02-10 15:00:00');

-- 预约8：电子产品3件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(8, 'electronics', 2, 'fair', '平板电脑', NULL, 1, 'C区-电子柜', 0, '2026-02-11 12:00:00', '2026-02-11 12:00:00'),
(8, 'electronics', 1, 'fair', '充电器配件', NULL, 1, 'C区-配件柜', 0, '2026-02-11 12:00:00', '2026-02-11 12:00:00');

-- 预约9：生活用品15件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(9, 'daily', 8, 'good', '文具用品', NULL, 1, 'D区-1号架', 0, '2026-02-11 16:00:00', '2026-02-11 16:00:00'),
(9, 'daily', 7, 'good', '日用品', NULL, 1, 'D区-2号架', 0, '2026-02-11 16:00:00', '2026-02-11 16:00:00');

-- 预约10：儿童读物18本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(10, 'book', 18, 'good', '儿童绘本和故事书', NULL, 1, 'A区-儿童专区', 0, '2026-02-09 11:00:00', '2026-02-09 11:00:00');

-- 预约11：夏季衣物20件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(11, 'clothing', 10, 'good', '夏季T恤', NULL, 1, 'B区-2号柜', 0, '2026-02-09 15:00:00', '2026-02-09 15:00:00'),
(11, 'clothing', 10, 'good', '夏季短裤', NULL, 1, 'B区-3号柜', 0, '2026-02-09 15:00:00', '2026-02-09 15:00:00');

-- 预约12：考研资料30本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(12, 'book', 15, 'good', '考研英语资料', NULL, 1, 'A区-考试专区-1', 0, '2026-02-08 11:00:00', '2026-02-08 11:00:00'),
(12, 'book', 15, 'good', '考研数学资料', NULL, 1, 'A区-考试专区-2', 0, '2026-02-08 11:00:00', '2026-02-08 11:00:00');

-- 预约13：厨房用品8件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(13, 'daily', 8, 'good', '厨具餐具', NULL, 1, 'D区-厨具专区', 0, '2026-02-08 16:00:00', '2026-02-08 16:00:00');

-- 为整理中的预约创建旧物记录（ID 14-20，共7条预约）
-- 预约14：电子配件5件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(14, 'electronics', 5, 'good', '数据线和充电器', NULL, 0, NULL, 0, '2026-02-07 11:00:00', '2026-02-07 11:00:00');

-- 预约15：文学作品22本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(15, 'book', 22, 'good', '中外文学名著', NULL, 0, NULL, 0, '2026-02-07 15:00:00', '2026-02-07 15:00:00');

-- 预约16：秋季衣物16件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(16, 'clothing', 16, 'good', '秋季外套和长袖', NULL, 0, NULL, 0, '2026-02-06 11:00:00', '2026-02-06 11:00:00');

-- 预约17：文体用品12件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(17, 'daily', 12, 'good', '体育用品和文具', NULL, 0, NULL, 0, '2026-02-06 16:00:00', '2026-02-06 16:00:00');

-- 预约18：技术书籍28本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(18, 'book', 28, 'good', '计算机和工程类书籍', NULL, 0, NULL, 0, '2026-02-05 11:00:00', '2026-02-05 11:00:00');

-- 预约19：旧耳机音响4件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(19, 'electronics', 4, 'fair', '耳机和小音响', NULL, 0, NULL, 0, '2026-02-05 15:00:00', '2026-02-05 15:00:00');

-- 预约20：冬季外套14件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(20, 'clothing', 14, 'good', '冬季羽绒服和棉衣', NULL, 0, NULL, 0, '2026-02-04 11:00:00', '2026-02-04 11:00:00');

-- 为已对接的预约创建旧物记录（ID 21-26，共6条预约）
-- 预约21：中小学教辅35本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(21, 'book', 20, 'good', '小学教辅', NULL, 2, 'A区-教辅专区-1', 0, '2026-02-03 11:00:00', '2026-02-03 12:00:00'),
(21, 'book', 15, 'good', '中学教辅', NULL, 2, 'A区-教辅专区-2', 0, '2026-02-03 11:00:00', '2026-02-03 12:00:00');

-- 预约22：儿童衣物18件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(22, 'clothing', 18, 'good', '儿童春秋装', NULL, 2, 'B区-儿童专区', 0, '2026-02-03 15:00:00', '2026-02-03 16:00:00');

-- 预约23：学习用品20件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(23, 'daily', 20, 'good', '文具和学习用品', NULL, 2, 'D区-学习用品', 0, '2026-02-02 11:00:00', '2026-02-02 12:00:00');

-- 预约24：科普读物40本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(24, 'book', 40, 'good', '少儿科普读物', NULL, 2, 'A区-科普专区', 0, '2026-02-02 15:00:00', '2026-02-02 16:00:00');

-- 预约25：学习机平板6件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(25, 'electronics', 6, 'good', '学习平板电脑', NULL, 2, 'C区-教育电子', 0, '2026-02-01 11:00:00', '2026-02-01 12:00:00');

-- 预约26：运动服装22件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(26, 'clothing', 22, 'good', '运动服和运动鞋', NULL, 2, 'B区-运动专区', 0, '2026-02-01 15:00:00', '2026-02-01 16:00:00');

-- 为已完成的预约创建旧物记录（ID 27-30，共4条预约）
-- 预约27：图书馆捐赠50本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(27, 'book', 50, 'good', '各类图书', NULL, 2, 'A区-综合区', 0, '2026-01-31 11:00:00', '2026-01-31 14:00:00');

-- 预约28：爱心衣物25件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(28, 'clothing', 25, 'good', '四季衣物', NULL, 2, 'B区-综合区', 0, '2026-01-31 15:00:00', '2026-01-31 17:00:00');

-- 预约29：生活物资30件
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(29, 'daily', 30, 'good', '生活日用品', NULL, 2, 'D区-综合区', 0, '2026-01-30 11:00:00', '2026-01-30 14:00:00');

-- 预约30：公益捐书45本
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(30, 'book', 45, 'good', '公益图书', NULL, 2, 'A区-公益专区', 0, '2026-01-30 15:00:00', '2026-01-30 17:00:00');

-- 额外添加一些独立的旧物记录，使总数达到100条
-- 书籍类（30条）
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(6, 'book', 5, 'good', '历史类书籍', NULL, 1, 'A区-3号架', 0, NOW(), NOW()),
(7, 'book', 8, 'good', '经济管理类', NULL, 1, 'A区-4号架', 0, NOW(), NOW()),
(8, 'book', 6, 'good', '艺术类书籍', NULL, 1, 'A区-5号架', 0, NOW(), NOW()),
(9, 'book', 7, 'good', '哲学类书籍', NULL, 1, 'A区-6号架', 0, NOW(), NOW()),
(10, 'book', 9, 'good', '医学类书籍', NULL, 1, 'A区-7号架', 0, NOW(), NOW());

-- 衣物类（25条）
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(11, 'clothing', 5, 'good', '男士衬衫', NULL, 1, 'B区-4号柜', 0, NOW(), NOW()),
(12, 'clothing', 6, 'good', '女士连衣裙', NULL, 1, 'B区-5号柜', 0, NOW(), NOW()),
(13, 'clothing', 4, 'good', '儿童外套', NULL, 1, 'B区-6号柜', 0, NOW(), NOW()),
(14, 'clothing', 7, 'good', '运动裤', NULL, 0, NULL, 0, NOW(), NOW()),
(15, 'clothing', 8, 'good', '毛衣', NULL, 0, NULL, 0, NOW(), NOW());

-- 电子产品类（20条）
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(16, 'electronics', 2, 'fair', '旧手机', NULL, 1, 'C区-手机柜', 0, NOW(), NOW()),
(17, 'electronics', 3, 'good', '键盘鼠标', NULL, 1, 'C区-配件柜-2', 0, NOW(), NOW()),
(18, 'electronics', 1, 'fair', 'U盘移动硬盘', NULL, 1, 'C区-存储柜', 0, NOW(), NOW()),
(19, 'electronics', 2, 'good', '台灯', NULL, 0, NULL, 0, NOW(), NOW()),
(20, 'electronics', 1, 'fair', '计算器', NULL, 0, NULL, 0, NOW(), NOW());

-- 生活用品类（25条）
INSERT INTO donation_item (appointment_id, item_type, quantity, item_condition, description, image_url, status, storage_location, deleted, create_time, update_time)
VALUES
(21, 'daily', 5, 'good', '水杯保温杯', NULL, 2, 'D区-日用品-1', 0, NOW(), NOW()),
(22, 'daily', 6, 'good', '床上用品', NULL, 2, 'D区-日用品-2', 0, NOW(), NOW()),
(23, 'daily', 4, 'good', '收纳盒', NULL, 2, 'D区-日用品-3', 0, NOW(), NOW()),
(24, 'daily', 7, 'good', '雨伞', NULL, 0, NULL, 0, NOW(), NOW()),
(25, 'daily', 8, 'good', '背包', NULL, 0, NULL, 0, NOW(), NOW());
