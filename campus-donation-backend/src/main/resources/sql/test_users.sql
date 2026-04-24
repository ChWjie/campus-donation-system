-- 校园旧物公益捐赠回收管理系统 - 测试用户数据
-- 所有用户的密码都是: 123456
-- BCrypt加密后的密码: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

USE campus_donation;

-- 使用 INSERT IGNORE 避免重复插入
-- 插入测试用户数据
INSERT IGNORE INTO sys_user (username, password, real_name, phone, email, user_type, status, create_time, update_time, deleted) VALUES
-- 1. 管理员账号
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000001', 'admin@campus.edu.cn', 4, 1, NOW(), NOW(), 0),

-- 2. 公益对接员账号
('operator1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张对接', '13800000002', 'operator1@campus.edu.cn', 3, 1, NOW(), NOW(), 0),
('operator2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李对接', '13800000003', 'operator2@campus.edu.cn', 3, 1, NOW(), NOW(), 0),

-- 3. 志愿者账号
('volunteer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王志愿', '13800000004', 'volunteer1@campus.edu.cn', 2, 1, NOW(), NOW(), 0),
('volunteer2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘志愿', '13800000005', 'volunteer2@campus.edu.cn', 2, 1, NOW(), NOW(), 0),
('volunteer3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈志愿', '13800000006', 'volunteer3@campus.edu.cn', 2, 1, NOW(), NOW(), 0),

-- 4. 捐赠者账号
('donor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵捐赠', '13800000007', 'donor1@campus.edu.cn', 1, 1, NOW(), NOW(), 0),
('donor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱捐赠', '13800000008', 'donor2@campus.edu.cn', 1, 1, NOW(), NOW(), 0),
('donor3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙捐赠', '13800000009', 'donor3@campus.edu.cn', 1, 1, NOW(), NOW(), 0),
('donor4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周捐赠', '13800000010', 'donor4@campus.edu.cn', 1, 1, NOW(), NOW(), 0);

-- 查询所有用户数据
SELECT 
    id,
    username,
    real_name,
    phone,
    email,
    CASE user_type
        WHEN 1 THEN '捐赠者'
        WHEN 2 THEN '志愿者'
        WHEN 3 THEN '公益对接员'
        WHEN 4 THEN '管理员'
    END AS user_type_name,
    CASE status
        WHEN 1 THEN '正常'
        WHEN 0 THEN '禁用'
    END AS status_name,
    create_time
FROM sys_user
WHERE deleted = 0
ORDER BY user_type DESC, id;
