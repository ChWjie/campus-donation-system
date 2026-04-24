<template>
  <div class="notification-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span>通知中心</span>
            <el-tag v-if="unreadCount > 0" type="danger" effect="dark">未读 {{ unreadCount }}</el-tag>
          </div>
          <el-button type="primary" plain @click="handleReadAll" :disabled="unreadCount === 0">
            全部标记已读
          </el-button>
        </div>
      </template>

      <el-empty v-if="notifications.length === 0" description="暂无通知" />

      <div v-else class="notice-list">
        <el-card
          v-for="item in notifications"
          :key="item.id"
          class="notice-item"
          shadow="hover"
          :class="{ unread: item.isRead === 0 }"
        >
          <div class="notice-row">
            <div class="notice-main">
              <div class="notice-title">
                <span>{{ item.title }}</span>
                <el-tag v-if="item.isRead === 0" type="warning" size="small">未读</el-tag>
              </div>
              <div class="notice-content">{{ item.content || '暂无内容' }}</div>
              <div class="notice-time">{{ formatTime(item.createTime) }}</div>
            </div>
            <div class="notice-actions">
              <el-button v-if="item.isRead === 0" type="primary" link @click="handleRead(item.id)">
                标记已读
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { getMyNotifications, getUnreadCount, markAllAsRead, markAsRead } from '@/api/notification'

const notifications = ref<any[]>([])
const unreadCount = ref(0)

const formatTime = (value: string) => dayjs(value).format('YYYY-MM-DD HH:mm')

const loadNotifications = async () => {
  notifications.value = await getMyNotifications()
}

const loadUnreadCount = async () => {
  unreadCount.value = await getUnreadCount()
}

const reload = async () => {
  try {
    await Promise.all([loadNotifications(), loadUnreadCount()])
  } catch {
    ElMessage.error('通知加载失败，请稍后重试')
  }
}

const handleRead = async (id: number) => {
  try {
    await markAsRead(id)
    ElMessage.success('已标记为已读')
    await reload()
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleReadAll = async () => {
  try {
    await markAllAsRead()
    ElMessage.success('已全部标记为已读')
    await reload()
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(async () => {
  await reload()
})
</script>

<style scoped>
.notification-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}

.notice-list {
  display: grid;
  gap: 12px;
}

.notice-item.unread {
  border-left: 4px solid #e6a23c;
}

.notice-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.notice-main {
  flex: 1;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.notice-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.notice-time {
  color: #909399;
  font-size: 13px;
}
</style>
