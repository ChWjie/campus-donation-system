import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建 Axios 实例
const request: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
})

// 请求拦截器
request.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const userStore = useUserStore()
        // 自动附加 JWT Token
        if (userStore.token) {
            config.headers = config.headers || {}
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config as any
    },
    (error) => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
            return response.data
        }

        const res = response.data

        // 统一处理响应
        if (!res || res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }

        return res.data
    },
    (error) => {
        console.error('响应错误:', error)

        // 处理 401 未授权
        if (error.response?.status === 401) {
            ElMessage.error('登录已过期,请重新登录')
            const userStore = useUserStore()
            userStore.logout()
            router.push('/login')
        } else if (error.response?.status === 403) {
            ElMessage.error('权限不足,禁止访问')
        } else {
            ElMessage.error(error.response?.data?.message || '网络异常,请稍后重试')
        }

        return Promise.reject(error)
    }
)

export default request
