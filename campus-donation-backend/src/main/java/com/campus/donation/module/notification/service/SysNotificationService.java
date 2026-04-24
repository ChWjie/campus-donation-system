package com.campus.donation.module.notification.service;

import com.campus.donation.module.notification.entity.SysNotification;

import java.util.List;

/**
 * 系统通知服务接口
 */
public interface SysNotificationService {

    /**
     * 发送通知
     */
    void sendNotice(Long userId, String title, String content, Integer type);

    /**
     * 查询我的通知
     */
    List<SysNotification> getMyNotifications(Long userId);

    /**
     * 标记为已读
     */
    void markAsRead(Long id, Long userId);

    /**
     * 获取未读数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 批量标记已读
     */
    void markAllAsRead(Long userId);
}
