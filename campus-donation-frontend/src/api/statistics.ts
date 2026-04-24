import request from './request'

/**
 * 数据统计API
 */

// 总体概览
export const getOverview = () => {
    return request.get('/statistics/overview')
}

// 月度趋势
export const getMonthlyTrend = () => {
    return request.get('/statistics/monthly')
}

// 旧物类型分布
export const getItemTypeDistribution = () => {
    return request.get('/statistics/item-type')
}

// 公益对接统计
export const getDockingStatistics = () => {
    return request.get('/statistics/docking')
}

// 个人统计
export const getUserStatistics = () => {
    return request.get('/statistics/user')
}
