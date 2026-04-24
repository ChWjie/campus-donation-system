-- 主测试数据导入脚本
-- 按顺序执行所有测试数据SQL

-- 1. 用户数据（已有）
SOURCE test_users.sql;

-- 2. 回收点数据（已有）
SOURCE test_stations.sql;

-- 3. 捐赠预约数据
SOURCE test_appointments.sql;

-- 4. 旧物记录数据
SOURCE test_items.sql;

-- 5. 公益对接数据
SOURCE test_docking.sql;

-- 6. 系统通知数据
SOURCE test_notifications.sql;

-- 验证数据导入
SELECT '=== 数据导入完成 ===' as message;
SELECT 'sys_user' as table_name, COUNT(*) as count FROM sys_user WHERE deleted = 0
UNION ALL
SELECT 'collection_station', COUNT(*) FROM collection_station WHERE deleted = 0
UNION ALL
SELECT 'donation_appointment', COUNT(*) FROM donation_appointment WHERE deleted = 0
UNION ALL
SELECT 'donation_item', COUNT(*) FROM donation_item WHERE deleted = 0
UNION ALL
SELECT 'charity_docking', COUNT(*) FROM charity_docking WHERE deleted = 0
UNION ALL
SELECT 'sys_notification', COUNT(*) FROM sys_notification WHERE deleted = 0;
