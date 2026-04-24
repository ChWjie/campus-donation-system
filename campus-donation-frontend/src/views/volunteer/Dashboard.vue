<template>
  <div class="volunteer-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>志愿者工作台</span>
          <el-icon><Checked /></el-icon>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="预约码确认" name="confirm">
          <el-form :model="form" label-width="100px" class="confirm-form">
            <el-form-item label="预约码" required>
              <el-input v-model="form.appointCode" placeholder="请输入预约码（如 D-XXXXXX）" clearable />
            </el-form-item>
            <el-form-item label="旧物类型">
              <el-select v-model="form.itemType" placeholder="可选：现场修正类型" clearable>
                <el-option label="书籍" value="book" />
                <el-option label="衣物" value="clothing" />
                <el-option label="电子产品" value="electronics" />
                <el-option label="生活用品" value="daily" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="完好度">
              <el-input v-model="form.itemCondition" placeholder="如：good/fair/poor" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="confirmAppointment" :loading="confirming">确认接收并生成旧物记录</el-button>
            </el-form-item>
          </el-form>

          <el-divider content-position="left">最近确认记录</el-divider>
          <el-table :data="recentConfirms" border stripe>
            <el-table-column prop="appointCode" label="预约码" width="160" />
            <el-table-column prop="donorName" label="捐赠者" width="120" />
            <el-table-column prop="itemType" label="旧物类型" width="120">
              <template #default="{ row }">
                <el-tag>{{ getItemTypeLabel(row.itemType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="itemQuantity" label="数量" width="80" />
            <el-table-column prop="stationName" label="回收点" />
            <el-table-column label="确认时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.updateTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="旧物整理入库" name="items">
          <div class="toolbar">
            <el-select v-model="itemStatusFilter" style="width: 200px" @change="loadItems">
              <el-option label="待整理" :value="0" />
              <el-option label="已入库" :value="1" />
            </el-select>
            <el-button @click="loadItems">刷新</el-button>
          </div>

          <el-table v-loading="loadingItems" :data="items" border stripe>
            <el-table-column prop="id" label="旧物ID" width="90" />
            <el-table-column prop="itemName" label="物品名称" width="140" />
            <el-table-column prop="itemType" label="类别" width="120">
              <template #default="{ row }">
                <el-tag>{{ getItemTypeLabel(row.itemType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="itemCondition" label="完好度" width="120" />
            <el-table-column prop="storageLocation" label="暂存位置" min-width="180">
              <template #default="{ row }">
                <el-input
                  v-model="row.storageLocation"
                  placeholder="例如：A区-1号架"
                  @blur="handleUpdateStorage(row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'">
                  {{ row.status === 1 ? '已入库' : '待整理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 0"
                  type="primary"
                  link
                  @click="markInStock(row)"
                >
                  标记入库
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Checked } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getVolunteerConfirms, volunteerConfirm } from '@/api/donation'
import { getItemsByStatus, updateItemStatus, updateItemStorage } from '@/api/item'

const activeTab = ref('confirm')
const confirming = ref(false)
const loadingItems = ref(false)
const itemStatusFilter = ref(0)

const recentConfirms = ref<any[]>([])
const items = ref<any[]>([])

const form = reactive({
  appointCode: '',
  itemType: '',
  itemCondition: ''
})

const itemTypeMap: Record<string, string> = {
  book: '书籍',
  clothing: '衣物',
  electronics: '电子产品',
  daily: '生活用品',
  other: '其他'
}

const getItemTypeLabel = (type: string) => itemTypeMap[type] || type
const formatDateTime = (dateTime: string) => dayjs(dateTime).format('YYYY-MM-DD HH:mm')

const loadRecentConfirms = async () => {
  recentConfirms.value = await getVolunteerConfirms()
}

const loadItems = async () => {
  loadingItems.value = true
  try {
    items.value = await getItemsByStatus(itemStatusFilter.value)
  } finally {
    loadingItems.value = false
  }
}

const confirmAppointment = async () => {
  if (!form.appointCode.trim()) {
    ElMessage.warning('请输入预约码')
    return
  }

  confirming.value = true
  try {
    const payload = {
      ...(form.itemType && { itemType: form.itemType }),
      ...(form.itemCondition && { itemCondition: form.itemCondition })
    }
    await volunteerConfirm(form.appointCode, payload)
    ElMessage.success('确认成功，已生成旧物明细')
    form.appointCode = ''
    form.itemType = ''
    form.itemCondition = ''
    await Promise.all([loadRecentConfirms(), loadItems()])
  } finally {
    confirming.value = false
  }
}

const markInStock = async (row: any) => {
  await updateItemStatus(row.id, 1)
  ElMessage.success('已标记为入库')
  await loadItems()
}

const handleUpdateStorage = async (row: any) => {
  if (!row.storageLocation) return
  await updateItemStorage(row.id, row.storageLocation)
}

onMounted(async () => {
  await Promise.all([loadRecentConfirms(), loadItems()])
})
</script>

<style scoped>
.volunteer-dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.confirm-form {
  max-width: 620px;
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 12px;
}
</style>
