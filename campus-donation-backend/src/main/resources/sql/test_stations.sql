-- 回收点测试数据
USE campus_donation;

INSERT IGNORE INTO collection_station (name, location, open_time, capacity, current_num, status, deleted, create_time) VALUES
('1号宿舍楼回收点', '1号宿舍楼一楼大厅', '周一至周日 08:00-22:00', 100, 0, 1, 0, NOW()),
('2号宿舍楼回收点', '2号宿舍楼一楼大厅', '周一至周日 08:00-22:00', 100, 0, 1, 0, NOW()),
('3号宿舍楼回收点', '3号宿舍楼一楼大厅', '周一至周日 08:00-22:00', 100, 0, 1, 0, NOW()),
('图书馆回收点', '图书馆一楼服务台', '周一至周五 08:00-20:00', 150, 0, 1, 0, NOW()),
('食堂回收点', '第一食堂门口', '周一至周日 07:00-21:00', 80, 0, 1, 0, NOW()),
('教学楼回收点', 'A教学楼一楼', '周一至周五 08:00-18:00', 60, 0, 1, 0, NOW());

SELECT * FROM collection_station WHERE deleted = 0;
