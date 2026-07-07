<template>
  <el-config-provider>
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>用户管理系统</h1>
          <el-menu mode="horizontal" :router="true" :default-active="$route.path" class="nav-menu">
            <el-menu-item index="/">用户列表</el-menu-item>
            <el-menu-item index="/comments">商品评论</el-menu-item>
          </el-menu>
          <div class="header-right" v-if="authUser">
            <span class="user-info">{{ authUser.username }}</span>
            <el-button type="danger" text size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-config-provider>
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAuthUser, clearAuth, type AuthUser } from './utils/auth'

const router = useRouter()
const authUser = ref<AuthUser | null>(null)

watchEffect(() => {
  authUser.value = getAuthUser()
})

function handleLogout() {
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.el-header {
  background-color: #409eff;
  color: white;
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  margin-right: 40px;
  font-size: 20px;
}

.nav-menu {
  flex: 1;
}

.el-menu--horizontal {
  background-color: transparent;
  border-bottom: none;
}

.el-menu--horizontal .el-menu-item {
  color: white;
  border-bottom-color: transparent;
}

.el-menu--horizontal .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.el-menu--horizontal .el-menu-item.is-active {
  color: white;
  border-bottom: 2px solid white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  color: white;
  font-size: 14px;
}

.el-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}
</style>
