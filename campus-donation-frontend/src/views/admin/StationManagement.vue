<template>
  <div class="station-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回收点管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            添加回收点
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="stations"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="location" label="位置" width="200" />
        <el-table-column prop="openTime" label="开放时间" width="180" />
        <el-table-column label="容量" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="(row.currentNum / row.capacity) * 100"
              :color="getCapacityColor(row.currentNum, row.capacity)"
            >
              <span>{{ row.currentNum }} / {{ row.capacity }}</span>
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editStation(row)" link>
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
              link
            >
              {{ row.status === 1 ? '关闭' : '开放' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteStation(row)" link>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑回收点' : '添加回收点'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入回收点名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置描述" />
        </el-form-item>
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="form.openTime" placeholder="例如：周一至周五 08:00-20:00" />
        </el-form-item>
        <el-form-item label="容量上限" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">开放</el-radio>
            <el-radio :label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAllStations, createStation, updateStation, deleteStation as deleteStationApi } from '@/api/station'

const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const stations = ref<any[]>([])
const formRef = ref<FormInstance>()

const form = reactive({
  id: null as number | null,
  name: '',
  location: '',
  openTime: '',
  capacity: 100,
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入回收点名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置描述', trigger: 'blur' }],
  openTime: [{ required: true, message: '请输入开放时间', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量上限', trigger: 'blur' }]
}

// 获取容量颜色
const getCapacityColor = (current: number, total: number) => {
  const percentage = (current / total) * 100
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#e6a23c'
  return '#f56c6c'
}

// 加载回收点列表
const loadStations = async () => {
  loading.value = true
  try {
    stations.value = await getAllStations()
  } catch (error) {
    ElMessage.error('加载回收点列表失败')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑回收点
const editStation = (row: any) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.location = row.location
  form.openTime = row.openTime
  form.capacity = row.capacity
  form.status = row.status
  dialogVisible.value = true
}

// 切换状态
const toggleStatus = async (row: any) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await updateStation(row.id, { ...row, status: newStatus })
    ElMessage.success('状态更新成功')
    loadStations()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 删除回收点
const deleteStation = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该回收点吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteStationApi(row.id)
    ElMessage.success('删除成功')
    loadStations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value && form.id) {
          await updateStation(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createStation(form)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadStations()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.name = ''
  form.location = ''
  form.openTime = ''
  form.capacity = 100
  form.status = 1
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadStations()
})
</script>

<style scoped>
.station-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
