<template>
  <div class="qualifications">
    <el-card class="filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="审核状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="realName" label="申请人" width="120" />
        <el-table-column prop="orgName" label="关联组织" min-width="150" />
        <el-table-column prop="creditCode" label="信用代码" width="180" />
        <el-table-column prop="certImage" label="资质图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.certImage"
              style="width: 50px; height: 50px"
              :src="row.certImage"
              :preview-src-list="[row.certImage]"
              preview-teleported
            />
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" link @click="handleAudit(row)">去审核</el-button>
            <el-button v-else type="info" link @click="handleViewDetail(row)">查看原因</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialog.visible" title="资质审核" width="500px">
      <el-form :model="auditDialog.form" label-width="80px">
        <el-form-item label="组织名称">
          <span>{{ auditDialog.currentUser.orgName }}</span>
        </el-form-item>
        <el-form-item label="资质图片">
          <el-image
            style="width: 200px; height: auto"
            :src="auditDialog.currentUser.certImage"
            :preview-src-list="[auditDialog.currentUser.certImage]"
            preview-teleported
          />
        </el-form-item>
        <el-form-item label="通过审核">
          <el-radio-group v-model="auditDialog.form.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditDialog.form.status === 2" label="驳回原因">
          <el-input v-model="auditDialog.form.reason" type="textarea" rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="auditDialog.loading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQualificationPage, auditQualification } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  current: 1,
  size: 10,
  status: null
})

const auditDialog = reactive({
  visible: false,
  loading: false,
  currentUser: {} as any,
  form: {
    status: 1,
    reason: ''
  }
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getQualificationPage(query)
    list.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleAudit = (row: any) => {
  auditDialog.currentUser = row
  auditDialog.form.status = 1
  auditDialog.form.reason = ''
  auditDialog.visible = true
}

const handleViewDetail = (row: any) => {
  if (row.status === 2) {
    ElMessageBox.alert(row.rejectReason || '未填写原因', '驳回原因')
  } else {
    ElMessage.info('该申请已通过审核')
  }
}

const submitAudit = async () => {
  auditDialog.loading = true
  try {
    await auditQualification(auditDialog.currentUser.id, auditDialog.form)
    ElMessage.success('审核已提交')
    auditDialog.visible = false
    handleQuery()
  } finally {
    auditDialog.loading = false
  }
}

const getStatusText = (status: number) => {
  const map: any = { 0: '待审核', 1: '已通过', 2: '已驳回' }
  return map[status] || '未知'
}

const getStatusTag = (status: number) => {
  const map: any = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.qualifications {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
