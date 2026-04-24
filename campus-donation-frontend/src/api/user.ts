import request from './request'

export interface LoginDTO {
    username: string
    password: string
}

export interface RegisterDTO {
    username: string
    password: string
    realName?: string
    phone?: string
    email?: string
    userType: number
}

export interface LoginVO {
    token: string
    userId: number
    username: string
    realName: string
    userType: number
}

/**
 * 用户登录
 */
export const login = (data: LoginDTO) => {
    return request.post<any, LoginVO>('/auth/login', data)
}

/**
 * 用户注册
 */
export const register = (data: RegisterDTO) => {
    return request.post<any, void>('/auth/register', data)
}
