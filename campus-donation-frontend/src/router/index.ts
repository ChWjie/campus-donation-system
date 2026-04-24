import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/views/auth/Login.vue'),
            meta: { title: '登录' }
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('@/views/auth/Register.vue'),
            meta: { title: '注册' }
        },
        // 捐赠者路由
        {
            path: '/donor',
            name: 'DonorLayout',
            component: () => import('@/layouts/DefaultLayout.vue'),
            meta: { requireAuth: true, roles: [1] },
            children: [
                {
                    path: 'dashboard',
                    name: 'DonorDashboard',
                    component: () => import('@/views/donor/Dashboard.vue'),
                    meta: { title: '捐赠者首页' }
                },
                {
                    path: 'appointments',
                    name: 'MyAppointments',
                    component: () => import('@/views/donor/MyAppointments.vue'),
                    meta: { title: '我的预约' }
                }
            ]
        },
        // 志愿者路由
        {
            path: '/volunteer',
            name: 'VolunteerLayout',
            component: () => import('@/layouts/DefaultLayout.vue'),
            meta: { requireAuth: true, roles: [2] },
            children: [
                {
                    path: 'dashboard',
                    name: 'VolunteerDashboard',
                    component: () => import('@/views/volunteer/Dashboard.vue'),
                    meta: { title: '志愿者首页' }
                }
            ]
        },
        // 公益对接员路由
        {
            path: '/charity',
            name: 'CharityLayout',
            component: () => import('@/layouts/DefaultLayout.vue'),
            meta: { requireAuth: true, roles: [3] },
            children: [
                {
                    path: 'dashboard',
                    name: 'CharityDashboard',
                    component: () => import('@/views/charity/Dashboard.vue'),
                    meta: { title: '公益对接员首页' }
                },
                {
                    path: 'qualification',
                    name: 'CharityQualification',
                    component: () => import('@/views/charity/Qualification.vue'),
                    meta: { title: '公益资质申请' }
                }
            ]
        },
        // 通知中心（所有登录用户）
        {
            path: '/notifications',
            name: 'NotificationsLayout',
            component: () => import('@/layouts/DefaultLayout.vue'),
            meta: { requireAuth: true, roles: [1, 2, 3, 4] },
            children: [
                {
                    path: '',
                    name: 'Notifications',
                    component: () => import('@/views/common/Notifications.vue'),
                    meta: { title: '通知中心' }
                }
            ]
        },
        // 管理员路由
        {
            path: '/admin',
            name: 'AdminLayout',
            component: () => import('@/layouts/DefaultLayout.vue'),
            meta: { requireAuth: true, roles: [4] },
            children: [
                {
                    path: 'dashboard',
                    name: 'AdminDashboard',
                    component: () => import('@/views/admin/Dashboard.vue'),
                    meta: { title: '管理员首页' }
                },
                {
                    path: 'stations',
                    name: 'StationManagement',
                    component: () => import('@/views/admin/StationManagement.vue'),
                    meta: { title: '回收点管理' }
                },
                {
                    path: 'users',
                    name: 'UserManagement',
                    component: () => import('@/views/admin/UserManagement.vue'),
                    meta: { title: '账号管理' }
                },
                {
                    path: 'qualifications',
                    name: 'Qualifications',
                    component: () => import('@/views/admin/Qualifications.vue'),
                    meta: { title: '资质审核' }
                },
                {
                    path: 'feedbacks',
                    name: 'Feedbacks',
                    component: () => import('@/views/admin/Feedbacks.vue'),
                    meta: { title: '反馈管理' }
                }
            ]
        },
        // 403 权限不足页面
        {
            path: '/403',
            name: 'Forbidden',
            component: () => import('@/views/error/403.vue'),
            meta: { title: '权限不足' }
        }
    ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
    const userStore = useUserStore()

    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 校园旧物公益捐赠回收管理系统`
    }

    // 需要认证的路由
    if (to.meta.requireAuth) {
        if (!userStore.isLoggedIn()) {
            ElMessage.warning('请先登录')
            next('/login')
            return
        }

        // 角色权限检查
        if (to.meta.roles && Array.isArray(to.meta.roles)) {
            if (!to.meta.roles.includes(userStore.userType)) {
                ElMessage.error('权限不足,无法访问该页面')
                next('/403')
                return
            }
        }
    }

    next()
})

export default router
