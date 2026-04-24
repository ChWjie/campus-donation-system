import request from './request'

/**
 * 公益对接API
 */

export interface CharityDockingDTO {
    charityName: string
    charityContact: string
    purpose: string
    itemIds: string
    itemCount: number
}

// 创建对接申请
export const createDocking = (data: CharityDockingDTO) => {
    return request.post('/charity/docking', data)
}

// 查询对接列表
export const getDockingList = (status?: number) => {
    return request.get('/charity/docking/list', { params: { status } })
}

// 查询对接详情
export const getDockingDetail = (id: number) => {
    return request.get(`/charity/docking/${id}`)
}

// 上传反馈
export const uploadFeedback = (id: number, feedbackText: string, feedbackImages: string) => {
    return request.put(`/charity/docking/${id}/feedback`, { feedbackText, feedbackImages })
}

// 完成对接
export const completeDocking = (id: number) => {
    return request.put(`/charity/docking/${id}/complete`)
}

// 月度对接统计
export const getMonthlyStatistics = () => {
    return request.get('/charity/statistics/monthly')
}

// 对接总数
export const getTotalCount = () => {
    return request.get('/charity/statistics/total')
}

// 获取已认证的公益组织列表
export const getVerifiedOrganizations = () => {
    return request.get('/charity/qualification/verified')
}

// 提交资质申请
export const submitQualification = (data: {
  orgName: string
  creditCode: string
  certImage: string
}) => {
  return request.post('/charity/qualification/submit', data)
}

// 查询我的资质状态
export const getQualificationStatus = () => {
  return request.get('/charity/qualification/status')
}
