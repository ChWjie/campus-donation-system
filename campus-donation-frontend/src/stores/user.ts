import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginVO } from '@/api/user'

export const useUserStore = defineStore('user', () => {
    // 状态
    const token = ref<string>(localStorage.getItem('token') || '')
    const userId = ref<number>(Number(localStorage.getItem('userId')) || 0)
    const username = ref<string>(localStorage.getItem('username') || '')
    const realName = ref<string>(localStorage.getItem('realName') || '')
    const userType = ref<number>(Number(localStorage.getItem('userType')) || 0)

    // 设置用户信息
    const setUserInfo = (loginVO: LoginVO) => {
        token.value = loginVO.token
        userId.value = loginVO.userId
        username.value = loginVO.username
        realName.value = loginVO.realName || ''
        userType.value = loginVO.userType

        // 持久化到 localStorage
        localStorage.setItem('token', loginVO.token)
        localStorage.setItem('userId', String(loginVO.userId))
        localStorage.setItem('username', loginVO.username)
        localStorage.setItem('realName', loginVO.realName || '')
        localStorage.setItem('userType', String(loginVO.userType))
    }

    // 清除用户信息
    const clearUserInfo = () => {
        token.value = ''
        userId.value = 0
        username.value = ''
        realName.value = ''
        userType.value = 0

        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        localStorage.removeItem('realName')
        localStorage.removeItem('userType')
    }

    // 登出
    const logout = () => {
        clearUserInfo()
    }

    // 判断是否已登录
    const isLoggedIn = () => {
        return !!token.value
    }

    // 获取用户类型名称
    const getUserTypeName = () => {
        const typeMap: Record<number, string> = {
            1: '捐赠者',
            2: '志愿者',
            3: '公益对接员',
            4: '管理员'
        }
        return typeMap[userType.value] || '未知'
    }

    return {
        token,
        userId,
        username,
        realName,
        userType,
        setUserInfo,
        clearUserInfo,
        logout,
        isLoggedIn,
        getUserTypeName
    }
})
