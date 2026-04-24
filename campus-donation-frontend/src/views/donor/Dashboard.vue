<template>
  <div class="donor-home">
    <el-card class="appointment-card">
      <template #header>
        <div class="card-header">
          <span>提交捐赠预约</span>
          <el-icon><DocumentAdd /></el-icon>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="appointment-form"
      >
        <el-form-item label="旧物类型" prop="itemType">
          <el-select v-model="form.itemType" placeholder="请选择旧物类型" style="width: 100%">
            <el-option label="书籍" value="book" />
            <el-option label="衣物" value="clothing" />
            <el-option label="电子产品" value="electronics" />
            <el-option label="生活用品" value="daily" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="数量" prop="itemQuantity">
          <el-input-number
            v-model="form.itemQuantity"
            :min="1"
            :max="100"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="完好度" prop="itemCondition">
          <el-radio-group v-model="form.itemCondition">
            <el-radio label="good">完好</el-radio>
            <el-radio label="fair">一般</el-radio>
            <el-radio label="poor">较差</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="旧物描述" prop="itemDesc">
          <el-input
            v-model="form.itemDesc"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您要捐赠的旧物..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="回收点" prop="stationId">
          <el-select
            v-model="form.stationId"
            placeholder="请选择回收点"
            style="width: 100%"
            :loading="stationLoading"
          >
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="`${station.name} (${station.location})`"
              :value="station.id"
            >
              <div class="station-option">
                <span>{{ station.name }}</span>
                <el-tag size="small" type="info">{{ station.openTime }}</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="预约时间" prop="appointTime">
          <el-date-picker
            v-model="form.appointTime"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
            :disabled-date="disabledDate"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting" size="large">
            提交预约
          </el-button>
          <el-button @click="resetForm" size="large">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预约成功对话框 -->
    <el-dialog
      v-model="showQrCode"
      title="预约成功"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="qrcode-dialog">
        <el-result icon="success" title="预约提交成功！">
          <template #sub-title>
            <p>请保存您的预约码，到回收点时出示给志愿者扫描</p>
          </template>
          <template #extra>
            <div class="qrcode-content">
              <div class="qrcode-image">
                <img :src="qrCodeData" alt="预约二维码" />
              </div>
              <div class="appoint-code">
                <el-text tag="b">预约码：{{ appointCode }}</el-text>
              </div>
              <el-button type="primary" @click="goToMyAppointments">
                查看我的预约
              </el-button>
            </div>
          </template>
        </el-result>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { DocumentAdd } from '@element-plus/icons-vue'
import { getAvailableStations } from '@/api/station'
import { createAppointment } from '@/api/donation'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const stationLoading = ref(false)
const stations = ref<any[]>([])
const showQrCode = ref(false)
const qrCodeData = ref('')
const appointCode = ref('')

// 表单数据
const form = reactive({
  itemType: '',
  itemQuantity: 1,
  itemCondition: 'good',
  itemDesc: '',
  stationId: null as number | null,
  appointTime: ''
})

// 表单验证规则
const rules: FormRules = {
  itemType: [{ required: true, message: '请选择旧物类型', trigger: 'change' }],
  itemQuantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  itemCondition: [{ required: true, message: '请选择完好度', trigger: 'change' }],
  itemDesc: [
    { required: true, message: '请输入旧物描述', trigger: 'blur' },
    { min: 10, message: '描述至少10个字符', trigger: 'blur' }
  ],
  stationId: [{ required: true, message: '请选择回收点', trigger: 'change' }],
  appointTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

// 禁用过去的日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7 // 不能选择今天之前的日期
}

// 加载可用回收点
const loadStations = async () => {
  stationLoading.value = true
  try {
    stations.value = await getAvailableStations()
  } catch (error) {
    ElMessage.error('加载回收点失败')
  } finally {
    stationLoading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const result = await createAppointment(form)
        ElMessage.success('预约提交成功！')
        
        // 显示二维码
        qrCodeData.value = result.qrCodeBase64
        appointCode.value = result.appointCode
        showQrCode.value = true
        
        // 重置表单
        resetForm()
      } catch (error) {
        console.error('提交预约失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
}

// 跳转到我的预约
const goToMyAppointments = () => {
  showQrCode.value = false
  router.push('/donor/appointments')
}

onMounted(() => {
  loadStations()
})
</script>

<style scoped>
.donor-home {
  padding: 20px;
}

.appointment-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.appointment-form {
  margin-top: 20px;
}

.station-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.qrcode-dialog {
  text-align: center;
}

.qrcode-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.qrcode-image img {
  width: 300px;
  height: 300px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
}

.appoint-code {
  font-size: 16px;
  color: #409eff;
}
</style>
