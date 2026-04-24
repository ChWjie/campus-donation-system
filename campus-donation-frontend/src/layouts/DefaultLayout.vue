<template>
  <div class="layout-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <h3>校园旧物公益捐赠回收管理系统</h3>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.realName || userStore.username }}
              <span class="user-type">({{ userStore.getUserTypeName() }})</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px" class="layout-aside">
          <el-menu
            :default-active="activeMenu"
            router
          >
            <!-- 捐赠者菜单 -->
            <template v-if="userStore.userType === 1">
              <el-menu-item index="/donor/dashboard">
                <el-icon><DocumentAdd /></el-icon>
                <span>提交预约</span>
              </el-menu-item>
              <el-menu-item index="/donor/appointments">
                <el-icon><List /></el-icon>
                <span>我的预约</span>
              </el-menu-item>
            </template>

            <!-- 志愿者菜单 -->
            <template v-if="userStore.userType === 2">
              <el-menu-item index="/volunteer/dashboard">
                <el-icon><Checked /></el-icon>
                <span>扫码确认</span>
              </el-menu-item>
            </template>

            <!-- 公益对接员菜单 -->
            <template v-if="userStore.userType === 3">
              <el-menu-item index="/charity/dashboard">
                <el-icon><Link /></el-icon>
                <span>公益对接</span>
              </el-menu-item>
              <el-menu-item index="/charity/qualification">
                <el-icon><DocumentChecked /></el-icon>
                <span>资质申请</span>
              </el-menu-item>
            </template>

            <!-- 管理员菜单 -->
            <template v-if="userStore.userType === 4">
              <el-menu-item index="/admin/dashboard">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
              </el-menu-item>
              <el-menu-item index="/admin/stations">
                <el-icon><Location /></el-icon>
                <span>回收点管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/users">
                <el-icon><User /></el-icon>
                <span>人员账号管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/qualifications">
                <el-icon><DocumentChecked /></el-icon>
                <span>公益资质审核</span>
              </el-menu-item>
              <el-menu-item index="/admin/feedbacks">
                <el-icon><ChatLineSquare /></el-icon>
                <span>受赠反馈管理</span>
              </el-menu-item>
            </template>

            <el-menu-item index="/notifications">
              <el-icon><Bell /></el-icon>
              <span>通知中心</span>
              <el-badge v-if="unreadCount > 0" :value="unreadCount" class="notice-badge" />
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="layout-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { 
  HomeFilled, 
  User, 
  SwitchButton, 
  DocumentAdd, 
  List, 
  Checked, 
  Link, 
  Location,
  DocumentChecked,
  ChatLineSquare,
  Bell
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getUnreadCount } from '@/api/notification'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const unreadCount = ref(0)

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}

const loadUnreadCount = async () => {
  if (!userStore.isLoggedIn()) return
  try {
    unreadCount.value = await getUnreadCount()
  } catch {
    unreadCount.value = 0
  }
}

onMounted(() => {
  loadUnreadCount()
})

watch(
  () => route.path,
  () => {
    loadUnreadCount()
  }
)
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-left h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.user-type {
  color: #909399;
  font-size: 12px;
}

.layout-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.layout-main {
  background: #f5f7fa;
  padding: 20px;
}

.notice-badge {
  margin-left: 8px;
}
</style>
