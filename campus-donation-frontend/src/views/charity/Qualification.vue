<template>
  <div class="qualification-page">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>公益资质申请</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
            <el-form-item label="组织名称" prop="orgName">
              <el-input v-model="form.orgName" placeholder="请输入组织名称" />
            </el-form-item>
            <el-form-item label="信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
            </el-form-item>
            <el-form-item label="资质图片URL" prop="certImage">
              <el-input v-model="form.certImage" placeholder="请填写资质材料图片链接" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card>
          <template #header>
            <span>当前审核状态</span>
          </template>
          <el-empty v-if="!statusData" description="暂无申请记录" />
          <el-descriptions v-else :column="2" border>
            <el-descriptions-item label="组织名称">{{ statusData.orgName }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <el-tag :type="statusType(statusData.status)">{{ statusText(statusData.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="信用代码">{{ statusData.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatTime(statusData.createTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="statusData.rejectReason" label="驳回原因" :span="2">
              {{ statusData.rejectReason }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import dayjs from 'dayjs'
import { getQualificationStatus, submitQualification } from '@/api/charity'

const formRef = ref<FormInstance>()
const submitting = ref(false)
const statusData = ref<any>(null)

const form = reactive({
  orgName: '',
  creditCode: '',
  certImage: ''
})

const rules: FormRules = {
  orgName: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  certImage: [{ required: true, message: '请填写资质图片链接', trigger: 'blur' }]
}

const formatTime = (value: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const statusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已驳回'
  }
  return map[status] || '未知'
}

const statusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return map[status] || 'info'
}

const loadStatus = async () => {
  statusData.value = await getQualificationStatus()
}

const resetForm = () => {
  form.orgName = ''
  form.creditCode = ''
  form.certImage = ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (!valid) return
    submitting.value = true
    try {
      await submitQualification(form)
      ElMessage.success('提交成功，请等待管理员审核')
      resetForm()
      await loadStatus()
    } finally {
      submitting.value = false
    }
  })
}

onMounted(async () => {
  await loadStatus()
})
</script>

<style scoped>
.qualification-page {
  padding: 20px;
}
</style>
