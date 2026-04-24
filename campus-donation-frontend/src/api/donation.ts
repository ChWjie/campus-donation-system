import request from './request'

/**
 * 捐赠预约API
 */

// 创建捐赠预约
export const createAppointment = (data: any) => {
    return request.post('/donation/appointment', data)
}

// 查询我的预约列表
export const getMyAppointments = () => {
    return request.get('/donation/appointment/my')
}

// 查询预约详情
export const getAppointmentDetail = (id: number) => {
    return request.get(`/donation/appointment/${id}`)
}

// 志愿者扫码/手动确认
export const volunteerConfirm = (code: string, data?: { itemType?: string, itemCondition?: string }) => {
    return request.post(`/donation/confirm/${code}`, data)
}

// 查询志愿者的确认记录
export const getVolunteerConfirms = () => {
    return request.get('/donation/appointment/volunteer/confirms')
}

// 更新预约状态
export const updateAppointmentStatus = (id: number, status: number) => {
    return request.put(`/donation/appointment/${id}/status`, { status })
}

// 取消预约
export const cancelAppointment = (id: number) => {
    return request.put(`/donation/appointment/${id}/cancel`)
}
