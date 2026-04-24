import request from './request'

/**
 * 回收点API
 */

// 查询所有回收点
export const getAllStations = () => {
    return request.get('/station/list')
}

// 查询可用回收点
export const getAvailableStations = () => {
    return request.get('/station/available')
}

// 根据ID查询回收点
export const getStationById = (id: number) => {
    return request.get(`/station/${id}`)
}

// 创建回收点（管理员）
export const createStation = (data: any) => {
    return request.post('/station', data)
}

// 更新回收点（管理员）
export const updateStation = (id: number, data: any) => {
    return request.put(`/station/${id}`, data)
}

// 删除回收点（管理员）
export const deleteStation = (id: number) => {
    return request.delete(`/station/${id}`)
}
