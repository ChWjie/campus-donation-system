<template>
  <div class="feedbacks">
    <el-card class="filter-card">
      <div class="header">
        <span class="title">受赠反馈管理</span>
        <el-radio-group v-model="query.status" @change="handleQuery">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">已对接</el-radio-button>
          <el-radio-button :label="2">已反馈</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-card class="table-card" v-loading="loading">
      <el-empty v-if="list.length === 0" description="暂无对接反馈" />
      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="item in list" :key="item.id" style="margin-bottom: 20px">
          <el-card shadow="hover" class="feedback-item">
            <template #header>
              <div class="card-header">
                <span class="org-name">{{ item.charityName }}</span>
                <el-tag :type="item.status === 2 ? 'success' : 'info'">
                  {{ item.status === 2 ? '已反馈' : '待反馈' }}
                </el-tag>
              </div>
            </template>
            <div class="content">
              <p class="purpose"><strong>用途：</strong>{{ item.purpose }}</p>
              <p class="items"><strong>物品数：</strong>{{ item.itemCount }}件</p>
              
              <div v-if="item.status === 2" class="feedback-content">
                <el-divider content-position="left">受赠方声音</el-divider>
                <p class="text">{{ item.feedbackText }}</p>
                <div class="images" v-if="item.feedbackImages">
                  <el-image
                    v-for="(img, index) in item.feedbackImages.split(',')"
                    :key="index"
                    style="width: 80px; height: 80px; margin-right: 5px; border-radius: 4px"
                    :src="img"
                    :preview-src-list="item.feedbackImages.split(',')"
                    preview-teleported
                  />
                </div>
              </div>
              <div v-else class="no-feedback">
                对接时间：{{ formatDate(item.dockingTime) }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getAdminDockingList } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const query = reactive({
  current: 1,
  size: 9,
  status: null as number | null
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getAdminDockingList(query)
    list.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const formatDate = (date: any) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.feedbacks {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 18px;
  font-weight: bold;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.org-name {
  font-weight: bold;
  font-size: 16px;
}
.feedback-item {
  height: 100%;
}
.content p {
  margin: 8px 0;
  font-size: 14px;
}
.text {
  color: #666;
  line-height: 1.6;
}
.no-feedback {
  color: #999;
  font-size: 13px;
  margin-top: 15px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
