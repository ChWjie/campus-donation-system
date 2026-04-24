import request from './request'

/**
 * 系统通知API
 */

// 查询我的通知
export const getMyNotifications = () => {
    return request.get('/notification/my')
}

// 标记为已读
export const markAsRead = (id: number) => {
    return request.put(`/notification/${id}/read`)
}

// 获取未读数量
export const getUnreadCount = () => {
    return request.get('/notification/unread/count')
}

// 全部标记为已读
export const markAllAsRead = () => {
    return request.put('/notification/read/all')
}
