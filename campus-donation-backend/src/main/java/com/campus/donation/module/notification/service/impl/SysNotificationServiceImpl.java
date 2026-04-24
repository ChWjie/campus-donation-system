package com.campus.donation.module.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.notification.entity.SysNotification;
import com.campus.donation.module.notification.mapper.SysNotificationMapper;
import com.campus.donation.module.notification.service.SysNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统通知服务实现类
 */
@Service
public class SysNotificationServiceImpl implements SysNotificationService {

    private final SysNotificationMapper notificationMapper;

    public SysNotificationServiceImpl(SysNotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public void sendNotice(Long userId, String title, String content, Integer type) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0); // 未读

        notificationMapper.insert(notification);
    }

    @Override
    public List<SysNotification> getMyNotifications(Long userId) {
        QueryWrapper<SysNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("deleted", 0)
                .orderByDesc("create_time");
        return notificationMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long userId) {
        SysNotification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该通知");
        }

        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        QueryWrapper<SysNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_read", 0)
                .eq("deleted", 0);
        return Math.toIntExact(notificationMapper.selectCount(wrapper));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        QueryWrapper<SysNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("is_read", 0)
                .eq("deleted", 0);

        List<SysNotification> notifications = notificationMapper.selectList(wrapper);
        for (SysNotification notification : notifications) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }
}
