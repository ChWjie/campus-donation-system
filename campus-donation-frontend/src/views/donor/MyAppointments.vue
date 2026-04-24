<template>
  <div class="my-appointments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
          <el-button type="primary" @click="goToCreate">
            <el-icon><Plus /></el-icon>
            新建预约
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="appointments"
        style="width: 100%"
        empty-text="暂无预约记录"
      >
        <el-table-column prop="appointCode" label="预约码" width="200" />
        <el-table-column prop="itemType" label="旧物类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getItemTypeLabel(row.itemType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="itemQuantity" label="数量" width="80" />
        <el-table-column prop="stationName" label="回收点" width="150" />
        <el-table-column prop="appointTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.appointTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewDetail(row.id)"
              link
            >
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="primary"
              size="small"
              @click="showQrCode(row)"
              link
            >
              查看二维码
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleCancel(row)"
              link
            >
              取消预约
            </el-button>
            <el-button
              v-if="row.status === 4"
              type="success"
              size="small"
              @click="showCertificate(row)"
              link
            >
              电子证明
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 预约详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="预约详情"
      width="600px"
    >
      <el-descriptions v-if="currentAppointment" :column="2" border>
        <el-descriptions-item label="预约码">
          {{ currentAppointment.appointCode }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ currentAppointment.statusDesc }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="旧物类型">
          {{ getItemTypeLabel(currentAppointment.itemType) }}
        </el-descriptions-item>
        <el-descriptions-item label="数量">
          {{ currentAppointment.itemQuantity }}
        </el-descriptions-item>
        <el-descriptions-item label="完好度">
          {{ getConditionLabel(currentAppointment.itemCondition) }}
        </el-descriptions-item>
        <el-descriptions-item label="回收点">
          {{ currentAppointment.stationName }}
        </el-descriptions-item>
        <el-descriptions-item label="预约时间" :span="2">
          {{ formatDateTime(currentAppointment.appointTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="旧物描述" :span="2">
          {{ currentAppointment.itemDesc }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentAppointment.volunteerName" label="负责志愿者" :span="2">
          {{ currentAppointment.volunteerName }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{ formatDateTime(currentAppointment.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 二维码对话框 -->
    <el-dialog
      v-model="qrcodeVisible"
      title="预约二维码"
      width="400px"
    >
      <div class="qrcode-container">
        <img v-if="currentQrCode" :src="currentQrCode" alt="预约二维码" />
        <p class="qrcode-tip">请在回收点出示此二维码给志愿者扫描</p>
      </div>
    </el-dialog>

    <!-- 电子证明对话框 -->
    <el-dialog
      v-model="certVisible"
      title="电子捐赠证明"
      width="600px"
    >
      <div class="certificate-container" id="cert-content" v-if="certificateData">
        <div class="cert-header">校园旧物公益捐赠接收证明</div>
        <div class="cert-body">
          <p>尊敬的 <span class="cert-highlight">{{ certificateData?.donorName || '爱心人士' }}</span>：</p>
          <p>证书编号：<span class="cert-highlight">{{ certificateData?.certNo }}</span></p>
          <p class="cert-text">
            感谢您于 {{ certificateData ? formatDateTime(certificateData.issueTime) : '' }} 通过本系统完成了爱心捐赠。您捐赠的 
            <span class="cert-highlight">{{ certificateData?.itemQuantity }}</span> 件 
            <span class="cert-highlight">{{ certificateData ? getItemTypeLabel(certificateData.itemType) : '' }}</span>
            已由志愿者成功接收，正在妥善处理及对接至公益组织。
          </p>
          <p class="cert-text">感谢您对环保与公益事业的无私奉献！</p>
        </div>
        <div class="cert-footer">
          <p>校园旧物公益捐赠回收平台</p>
          <p>{{ dayjs().format('YYYY年 MM月 DD日') }}</p>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="downloadCertificateFile">下载 PDF 证明</el-button>
        <el-button @click="certVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { cancelAppointment, getMyAppointments, getAppointmentDetail } from '@/api/donation'
import { getCertificateByAppointment, downloadCertificate } from '@/api/certificate'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const appointments = ref<any[]>([])
const detailVisible = ref(false)
const qrcodeVisible = ref(false)
const certVisible = ref(false)
const currentAppointment = ref<any>(null)
const certificateData = ref<any>(null)
const currentQrCode = ref('')

// 旧物类型映射
const itemTypeMap: Record<string, string> = {
  book: '书籍',
  clothing: '衣物',
  electronics: '电子产品',
  daily: '生活用品',
  other: '其他'
}

// 完好度映射
const conditionMap: Record<string, string> = {
  good: '完好',
  fair: '一般',
  poor: '较差'
}

// 获取旧物类型标签
const getItemTypeLabel = (type: string) => {
  return itemTypeMap[type] || type
}

// 获取完好度标签
const getConditionLabel = (condition: string) => {
  return conditionMap[condition] || condition
}

// 获取状态类型
const getStatusType = (status: number) => {
  const typeMap: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
}

// 加载预约列表
const loadAppointments = async () => {
  loading.value = true
  try {
    appointments.value = await getMyAppointments()
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = async (id: number) => {
  try {
    currentAppointment.value = await getAppointmentDetail(id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('加载预约详情失败')
  }
}

// 显示二维码
const showQrCode = async (appointment: any) => {
  try {
    const detail = await getAppointmentDetail(appointment.id)
    currentQrCode.value = detail.qrCodeBase64
    qrcodeVisible.value = true
  } catch (error) {
    ElMessage.error('加载二维码失败')
  }
}

// 跳转到创建预约
const goToCreate = () => {
  router.push('/donor/dashboard')
}

// 显示电子证明
const showCertificate = async (appointment: any) => {
  try {
    certificateData.value = await getCertificateByAppointment(appointment.id)
    certVisible.value = true
  } catch (error) {
    ElMessage.error('加载证明失败')
  }
}

const downloadCertificateFile = async () => {
  if (!certificateData.value?.id) return
  try {
    const blob = await downloadCertificate(certificateData.value.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `donation-certificate-${certificateData.value.certNo || 'unknown'}.pdf`
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载证明失败')
  }
}

const handleCancel = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认取消该预约吗？取消后需重新提交预约。', '提示', {
      confirmButtonText: '确认取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    await cancelAppointment(row.id)
    ElMessage.success('预约已取消')
    await loadAppointments()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消预约失败')
    }
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.my-appointments {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.qrcode-container {
  text-align: center;
}

.qrcode-container img {
  width: 300px;
  height: 300px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  margin-bottom: 20px;
}

.qrcode-tip {
  color: #909399;
  font-size: 14px;
}

/* 电子证明样式 */
.certificate-container {
  padding: 40px;
  border: 4px double #dcdfe6;
  background-color: #fdfdfd;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" opacity="0.05"><text x="50" y="50" font-size="20" transform="rotate(-45 50 50)" fill="%23000">公益捐赠</text></svg>');
}

.cert-header {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #c0392b;
  margin-bottom: 30px;
  letter-spacing: 2px;
}

.cert-body {
  font-size: 16px;
  line-height: 2;
  color: #333;
}

.cert-text {
  text-indent: 2em;
  margin: 20px 0;
}

.cert-highlight {
  font-weight: bold;
  color: #c0392b;
  margin: 0 5px;
  text-decoration: underline;
}

.cert-footer {
  margin-top: 50px;
  text-align: right;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
