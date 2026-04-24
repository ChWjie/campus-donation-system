-- 校园旧物公益捐赠回收管理系统 - 数据库初始化脚本
-- MySQL 8.x

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_donation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_donation;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    phone       VARCHAR(20)  COMMENT '手机号',
    email       VARCHAR(100) COMMENT '邮箱',
    user_type   TINYINT      NOT NULL COMMENT '用户类型: 1-捐赠者 2-志愿者 3-公益对接员 4-管理员',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除: 1-已删除 0-未删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 回收点表
CREATE TABLE IF NOT EXISTS collection_station (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回收点ID',
    name        VARCHAR(100) NOT NULL COMMENT '回收点名称',
    location    VARCHAR(200) COMMENT '位置描述(如:3号宿舍楼一楼)',
    open_time   VARCHAR(100) COMMENT '开放时间(如:周一至周五 08:00-20:00)',
    capacity    INT          DEFAULT 100 COMMENT '容量上限',
    current_num INT          DEFAULT 0 COMMENT '当前存量',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1-开放 0-关闭',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收点信息表';

-- 3. 捐赠预约表
CREATE TABLE IF NOT EXISTS donation_appointment (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    donor_id        BIGINT       NOT NULL COMMENT '捐赠者ID',
    appoint_code    VARCHAR(32)  NOT NULL UNIQUE COMMENT '预约码',
    item_type       VARCHAR(50)  NOT NULL COMMENT '旧物类型: book/clothing/electronics/furniture/other',
    item_quantity   INT          NOT NULL COMMENT '数量',
    item_condition  VARCHAR(20)  COMMENT '完好度: good/fair/poor',
    item_desc       VARCHAR(500) COMMENT '旧物描述',
    station_id      BIGINT       NOT NULL COMMENT '回收点ID',
    appoint_time    DATETIME     NOT NULL COMMENT '预约回收时间',
    status          TINYINT      DEFAULT 0 COMMENT '状态: 0-待接收 1-已接收 2-整理中 3-已对接 4-已完成 5-已取消',
    volunteer_id    BIGINT       COMMENT '负责志愿者ID',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_donor_id (donor_id),
    INDEX idx_appoint_code (appoint_code),
    INDEX idx_station_id (station_id),
    INDEX idx_status (status),
    INDEX idx_volunteer_id (volunteer_id),
    INDEX idx_appoint_time (appoint_time),
    FOREIGN KEY (donor_id) REFERENCES sys_user(id),
    FOREIGN KEY (station_id) REFERENCES collection_station(id),
    FOREIGN KEY (volunteer_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='捐赠预约表';

-- 4. 旧物详情表
CREATE TABLE IF NOT EXISTS donation_item (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '旧物ID',
    appointment_id  BIGINT       NOT NULL COMMENT '关联预约ID',
    item_name       VARCHAR(100) COMMENT '物品名称',
    item_type       VARCHAR(50)  COMMENT '分类',
    quantity        INT          DEFAULT 1 COMMENT '数量',
    item_condition  VARCHAR(20)  COMMENT '完好度描述',
    status          TINYINT      DEFAULT 0 COMMENT '存储状态: 0-待整理 1-已入库 2-已对接 3-环保回收',
    image_url       VARCHAR(255) COMMENT '旧物图片URL',
    description     VARCHAR(500) COMMENT '备注',
    storage_location VARCHAR(100) COMMENT '暂存位置',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_item_type (item_type),
    INDEX idx_status (status),
    FOREIGN KEY (appointment_id) REFERENCES donation_appointment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旧物详情表';

-- 5. 公益对接表
CREATE TABLE IF NOT EXISTS charity_docking (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对接ID',
    operator_id     BIGINT       NOT NULL COMMENT '对接员ID',
    docking_no      VARCHAR(50)  NOT NULL UNIQUE COMMENT '对接编号',
    charity_name    VARCHAR(100) NOT NULL COMMENT '受赠组织名称',
    charity_contact VARCHAR(100) COMMENT '受赠方联系方式',
    item_ids        VARCHAR(500) COMMENT '对接旧物ID列表(JSON数组)',
    item_count      INT          DEFAULT 0 COMMENT '对接件数',
    purpose         VARCHAR(500) COMMENT '用途说明',
    status          TINYINT      DEFAULT 0 COMMENT '状态: 0-申请中 1-已对接 2-已反馈',
    feedback_text   TEXT         COMMENT '受赠方反馈',
    feedback_images VARCHAR(500) COMMENT '受赠方照片URL',
    docking_time    DATETIME     COMMENT '对接完成时间',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_operator_id (operator_id),
    INDEX idx_status (status),
    INDEX idx_docking_time (docking_time),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公益对接记录表';

-- 6. 公益资质审核表
CREATE TABLE IF NOT EXISTS charity_qualification (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资质ID',
    user_id         BIGINT       NOT NULL COMMENT '申请人ID',
    org_name        VARCHAR(100) NOT NULL COMMENT '组织名称',
    credit_code     VARCHAR(100) NOT NULL COMMENT '统一社会信用代码',
    cert_image      VARCHAR(255) COMMENT '资质证明图片',
    status          TINYINT      DEFAULT 0 COMMENT '状态: 0-待审核 1-已通过 2-已驳回',
    reject_reason   VARCHAR(500) COMMENT '驳回原因',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公益资质审核表';

-- 7. 捐赠证明表
CREATE TABLE IF NOT EXISTS donation_certificate (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '证明ID',
    donor_id        BIGINT       NOT NULL COMMENT '捐赠者ID',
    appointment_id  BIGINT       NOT NULL COMMENT '关联预约ID',
    cert_no         VARCHAR(50)  NOT NULL UNIQUE COMMENT '证书编号',
    cert_url        VARCHAR(255) COMMENT 'PDF文件路径',
    issue_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '颁发时间',
    deleted         TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_donor_id (donor_id),
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_cert_no (cert_no),
    FOREIGN KEY (donor_id) REFERENCES sys_user(id),
    FOREIGN KEY (appointment_id) REFERENCES donation_appointment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电子捐赠证明表';

-- 8. 系统通知表
CREATE TABLE IF NOT EXISTS sys_notification (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    title       VARCHAR(100) NOT NULL COMMENT '通知标题',
    content     TEXT         COMMENT '通知内容',
    type        TINYINT      COMMENT '类型: 1-预约提醒 2-接收确认 3-对接完成 4-公益反馈',
    is_read     TINYINT      DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';

-- 初始化数据

-- 插入管理员账号 (密码: 123456, BCrypt加密后的值)
INSERT INTO sys_user (username, password, real_name, user_type, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 4, 1);

-- 插入测试回收点
INSERT INTO collection_station (name, location, open_time, capacity, current_num, status) VALUES
('1号宿舍楼回收点', '1号宿舍楼一楼大厅', '周一至周日 08:00-22:00', 100, 0, 1),
('图书馆回收点', '图书馆一楼服务台', '周一至周五 09:00-21:00', 150, 0, 1),
('食堂回收点', '第一食堂门口', '周一至周日 07:00-20:00', 80, 0, 1);

-- 创建完成提示
SELECT '数据库初始化完成!' AS message;
