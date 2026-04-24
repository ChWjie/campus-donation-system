import request from './request'

// 用户管理
export function getUserPage(params: any) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export function updateUserStatus(id: number | string, status: number) {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function updateUserRole(id: number | string, userType: number) {
  return request({
    url: `/user/${id}/role`,
    method: 'put',
    params: { userType }
  })
}

export function deleteUser(id: number | string) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 资质审核
export function getQualificationPage(params: any) {
  return request({
    url: '/charity/qualification/page',
    method: 'get',
    params
  })
}

export function auditQualification(id: number | string, data: any) {
  return request({
    url: `/charity/qualification/${id}/audit`,
    method: 'put',
    data
  })
}

// 反馈管理 (管理员查询对接列表)
export function getAdminDockingList(params: any) {
  return request({
    url: '/charity/docking/admin/list',
    method: 'get',
    params
  })
}
