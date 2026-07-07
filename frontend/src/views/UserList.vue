<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi, type User } from '../api/user'

const loading = ref(false)
const users = ref<User[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const formData = reactive({
  username: '',
  email: '',
  phone: '',
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

/** 加载用户列表 */
async function fetchUsers() {
  loading.value = true
  try {
    users.value = await userApi.getAll()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

/** 打开新增对话框 */
function openCreateDialog() {
  isEdit.value = false
  editingId.value = null
  formData.username = ''
  formData.email = ''
  formData.phone = ''
  dialogVisible.value = true
}

/** 打开编辑对话框 */
function openEditDialog(user: User) {
  isEdit.value = true
  editingId.value = user.id!
  formData.username = user.username
  formData.email = user.email
  formData.phone = user.phone || ''
  dialogVisible.value = true
}

/** 提交表单 */
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  submitting.value = true
  try {
    if (isEdit.value && editingId.value !== null) {
      await userApi.update(editingId.value, { ...formData })
      ElMessage.success('更新成功')
    } else {
      await userApi.create({ ...formData })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchUsers()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

/** 删除用户 */
async function handleDelete(user: User) {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${user.username}」吗？`, '提示', {
      type: 'warning',
    })
    await userApi.delete(user.id!)
    ElMessage.success('删除成功')
    await fetchUsers()
  } catch (error) {
    // 用户取消对话框不报错，API 拦截器已处理网络/业务错误提示
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-list {
  margin-top: 20px;
}
</style>
