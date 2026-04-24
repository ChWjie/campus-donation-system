<template>
  <div class="user-management">
    <el-card class="filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="账号/姓名/手机号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="query.userType" placeholder="全部" clearable style="width: 150px">
            <el-option label="捐赠者" :value="1" />
            <el-option label="志愿者" :value="2" />
            <el-option label="公益对接员" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="userType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)">{{ getUserTypeText(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEditRole(row)">修改角色</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 修改角色弹窗 -->
    <el-dialog v-model="roleDialog.visible" title="修改用户角色" width="400px">
      <el-form :model="roleDialog.form" label-width="80px">
        <el-form-item label="用户">
          <span>{{ roleDialog.form.username }}</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="roleDialog.form.userType" placeholder="请选择" style="width: 100%">
            <el-option label="捐赠者" :value="1" />
            <el-option label="志愿者" :value="2" />
            <el-option label="公益对接员" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitRole" :loading="roleDialog.loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, updateUserStatus, updateUserRole, deleteUser } from '@/api/admin'

const loading = ref(false)
const userList = ref<any[]>([])
const total = ref(0)
const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  userType: null as number | null
})

const roleDialog = reactive({
  visible: false,
  loading: false,
  form: {
    id: null as number | string | null,
    username: '',
    userType: null as number | null
  }
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res: any = await getUserPage(query)
    userList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.keyword = ''
  query.userType = null
  handleQuery()
}

const handleStatusChange = async (row: any, val: any) => {
  try {
    await updateUserStatus(row.id, val as number)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = val === 1 ? 0 : 1
  }
}

const handleEditRole = (row: any) => {
  roleDialog.form.id = row.id
  roleDialog.form.username = row.username
  roleDialog.form.userType = row.userType
  roleDialog.visible = true
}

const submitRole = async () => {
  if (roleDialog.form.id === null || roleDialog.form.userType === null) return
  roleDialog.loading = true
  try {
    await updateUserRole(roleDialog.form.id, roleDialog.form.userType)
    ElMessage.success('角色更新成功')
    roleDialog.visible = false
    handleQuery()
  } finally {
    roleDialog.loading = false
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该用户吗？删除后不可恢复', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const getUserTypeText = (type: number) => {
  const map: any = {
    1: '捐赠者',
    2: '志愿者',
    3: '公益对接员',
    4: '管理员'
  }
  return map[type] || '未知'
}

const getUserTypeTag = (type: number) => {
  const map: any = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return map[type] || ''
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}
.filter-card {
  margin-bottom: 20px;
}
.table-card {
  min-height: 500px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
