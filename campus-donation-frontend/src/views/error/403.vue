<template>
  <div class="error-page">
    <el-result
      icon="error"
      title="403"
      sub-title="抱歉,您没有权限访问该页面"
    >
      <template #extra>
        <el-button type="primary" @click="$router.back()">返回上一页</el-button>
        <el-button @click="$router.push('/login')">返回登录</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
// 403 Forbidden Page
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
</style>
