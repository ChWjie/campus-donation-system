<template>
  <div class="charity-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>公益对接管理</h3>
          <el-button type="primary" :icon="Plus" @click="openDockingDialog" :disabled="selectedItems.length === 0">
            发起对接 (已选 {{ selectedItems.length }} 件)
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 待对接旧物大厅 -->
        <el-tab-pane label="可捐赠旧物大厅" name="items">
          <el-table
            v-loading="loadingItems"
            :data="availableItems"
            @selection-change="handleSelectionChange"
            border
            stripe
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="旧物ID" width="80" />
            <el-table-column prop="itemName" label="物品名称" />
            <el-table-column prop="itemType" label="分类">
              <template #default="{ row }">
                <el-tag>{{ getItemTypeLabel(row.itemType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="itemCondition" label="完好度" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="入库时间">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 我的对接记录 -->
        <el-tab-pane label="对接记录" name="records">
          <el-table v-loading="loadingRecords" :data="dockingRecords" border stripe>
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="charityName" label="公益组织名称" />
            <el-table-column prop="charityContact" label="联系方式" />
            <el-table-column prop="itemCount" label="捐赠数量" width="100" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发起时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="200">
              <template #default="{ row }">
                <el-button v-if="row.status !== 2" link type="primary" size="small" @click="openFeedbackDialog(row)">
                  上传反馈
                </el-button>
                <el-button v-if="row.status === 0" link type="success" size="small" @click="handleComplete(row.id)">
                  完成对接
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 发起对接对话框 -->
    <el-dialog v-model="dockingDialogVisible" title="发起公益对接" width="550px">
      <el-form :model="dockingForm" label-width="100px" ref="dockingFormRef">
        <el-form-item label="选择受赠方" required>
          <el-select
            v-model="dockingForm.charityName"
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入受赠机构"
            @change="handleOrgChange"
            style="width: 100%"
          >
            <el-option
              v-for="org in verifiedOrgs"
              :key="org.id"
              :label="org.orgName"
              :value="org.orgName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" required>
          <el-input v-model="dockingForm.charityContact" placeholder="受赠方联系人和电话" />
        </el-form-item>
        <el-form-item label="捐赠用途">
          <el-input v-model="dockingForm.purpose" type="textarea" :rows="3" placeholder="简述捐赠物资用途" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dockingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDocking" :loading="submitting">确认发起</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 上传反馈对话框 -->
    <el-dialog v-model="feedbackDialogVisible" title="填写受赠反馈" width="500px">
      <el-form :model="feedbackForm" label-width="100px">
        <el-form-item label="图文反馈">
          <el-input v-model="feedbackForm.feedbackText" type="textarea" :rows="4" placeholder="请记录对接情况、受赠方感言等" />
        </el-form-item>
        <el-form-item label="照片链接">
          <el-input v-model="feedbackForm.feedbackImages" placeholder="在此填入照片链接地址（暂时手动输入）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="submittingFeedback">提交反馈</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getItemsByStatus } from '@/api/item'
import { createDocking, getDockingList, uploadFeedback, completeDocking, getVerifiedOrganizations } from '@/api/charity'

const activeTab = ref('items')
const availableItems = ref<any[]>([])
const dockingRecords = ref<any[]>([])
const selectedItems = ref<any[]>([])
const loadingItems = ref(false)
const loadingRecords = ref(false)

// Dialog states
const dockingDialogVisible = ref(false)
const submitting = ref(false)
const dockingForm = reactive({
  charityName: '',
  charityContact: '',
  purpose: ''
})

const feedbackDialogVisible = ref(false)
const submittingFeedback = ref(false)
const currentDockingId = ref<number | null>(null)
const feedbackForm = reactive({
  feedbackText: '',
  feedbackImages: ''
})

const verifiedOrgs = ref<any[]>([])

const itemTypeMap: Record<string, string> = {
  book: '书籍',
  clothing: '衣物',
  electronics: '电子产品',
  daily: '生活用品',
  other: '其他'
}

const getItemTypeLabel = (type: string) => itemTypeMap[type] || type
const formatTime = (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm')

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'info',
    2: 'success'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status: number) => {
  const map: Record<number, string> = {
    0: '申请中',
    1: '已对接',
    2: '已反馈'
  }
  return map[status] || '未知状态'
}

// 加载可捐赠的旧物（仅展示已入库数据）
const loadAvailableItems = async () => {
  loadingItems.value = true
  try {
    const res: any = await getItemsByStatus(1)
    availableItems.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loadingItems.value = false
  }
}

// 加载对接记录
const loadDockingRecords = async () => {
  loadingRecords.value = true
  try {
    const res: any = await getDockingList()
    dockingRecords.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loadingRecords.value = false
  }
}

const loadVerifiedOrgs = async () => {
  try {
    const res: any = await getVerifiedOrganizations()
    verifiedOrgs.value = res || []
  } catch (error) {
    console.error(error)
  }
}

const handleOrgChange = (val: string) => {
  const org = verifiedOrgs.value.find(o => o.orgName === val)
  if (org) {
    // 假设资质中有联系方式，如果没有，后端资质表可以加，
    // 或者目前手动输入一次后系统记忆 (此处先根据现有字段逻辑)
    // 根据 schema.sql，charity_qualification 目前没有单独的手机号字段，
    // 但我们可以通过 user_id 查到该用户的手机号，或者资质中的 credit_code 等。
    // 这里先保留手动输入联系方式，但自动带入机构名
  }
}

const handleSelectionChange = (val: any[]) => {
  selectedItems.value = val
}

const openDockingDialog = () => {
  if (selectedItems.value.length === 0) return
  dockingForm.charityName = ''
  dockingForm.charityContact = ''
  dockingForm.purpose = ''
  dockingDialogVisible.value = true
}

const submitDocking = async () => {
  if (!dockingForm.charityName || !dockingForm.charityContact) {
    ElMessage.warning('请填写受赠方名称和联系方式')
    return
  }
  
  submitting.value = true
  try {
    const itemIds = selectedItems.value.map(item => item.id).join(',')
    const dto = {
      ...dockingForm,
      itemIds,
      itemCount: selectedItems.value.length
    }
    
    await createDocking(dto)
    ElMessage.success('发起对接成功')
    dockingDialogVisible.value = false
    selectedItems.value = []
    
    // Refresh both tables
    loadAvailableItems()
    loadDockingRecords()
    activeTab.value = 'records'
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const openFeedbackDialog = (row: any) => {
  currentDockingId.value = row.id
  feedbackForm.feedbackText = ''
  feedbackForm.feedbackImages = ''
  feedbackDialogVisible.value = true
}

const submitFeedback = async () => {
  if (!currentDockingId.value) return
  submittingFeedback.value = true
  try {
    await uploadFeedback(currentDockingId.value, feedbackForm.feedbackText, feedbackForm.feedbackImages)
    ElMessage.success('反馈上传成功')
    feedbackDialogVisible.value = false
    loadDockingRecords()
  } catch (error) {
    console.error(error)
  } finally {
    submittingFeedback.value = false
  }
}

const handleComplete = (id: number) => {
  ElMessageBox.confirm('是否确定完成本次对接（完成前请确保已上传必要的反馈）？', '提示', {
    confirmButtonText: '确定完成',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await completeDocking(id)
      ElMessage.success('对接已标记为完成')
      loadDockingRecords()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadAvailableItems()
  loadDockingRecords()
  loadVerifiedOrgs()
})
</script>

<style scoped>
.charity-dashboard {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
