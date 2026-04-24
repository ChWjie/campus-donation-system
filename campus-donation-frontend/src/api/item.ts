import request from './request'

/**
 * 旧物管理API
 */

// 从预约创建旧物记录
export const createItemsFromAppointment = (appointmentId: number) => {
    return request.post(`/item/create/${appointmentId}`)
}

// 根据预约ID查询旧物
export const getItemsByAppointment = (appointmentId: number) => {
    return request.get(`/item/appointment/${appointmentId}`)
}

// 根据状态查询旧物列表
export const getItemsByStatus = (status?: number) => {
    return request.get('/item/list', { params: { status } })
}

// 更新旧物状态
export const updateItemStatus = (id: number, status: number) => {
    return request.put(`/item/${id}/status`, { status })
}

// 更新仓库位置
export const updateItemStorage = (id: number, location: string) => {
    return request.put(`/item/${id}/storage`, { location })
}

// 上传旧物图片
export const uploadItemImage = (id: number, file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/item/${id}/image`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 旧物类型统计
export const getItemTypeStatistics = () => {
    return request.get('/item/statistics/type')
}
