package com.campus.donation.module.notification.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.notification.entity.SysNotification;
import com.campus.donation.module.notification.service.SysNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统通知控制器
 */
@Tag(name = "系统通知")
@RestController
@RequestMapping("/notification")
public class SysNotificationController {

    private final SysNotificationService notificationService;

    public SysNotificationController(SysNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "查询我的通知")
    @GetMapping("/my")
    public R<List<SysNotification>> getMyNotifications(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<SysNotification> notifications = notificationService.getMyNotifications(userId);
        return R.ok(notifications);
    }

    @Operation(summary = "标记为已读")
    @PutMapping("/{id}/read")
    public R<String> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAsRead(id, userId);
        return R.ok("标记成功");
    }

    @Operation(summary = "获取未读数量")
    @GetMapping("/unread/count")
    public R<Integer> getUnreadCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Integer count = notificationService.getUnreadCount(userId);
        return R.ok(count);
    }

    @Operation(summary = "全部标记为已读")
    @PutMapping("/read/all")
    public R<String> markAllAsRead(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAllAsRead(userId);
        return R.ok("全部标记成功");
    }
}
