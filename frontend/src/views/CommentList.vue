<template>
  <div class="comment-list">
    <!-- 提交评论表单 -->
    <el-card class="form-card">
      <template #header>
        <span>发表新评论</span>
      </template>
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="商品 ID" prop="productId">
          <el-input-number v-model="formData.productId" :min="1" placeholder="请输入商品 ID" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="formData.rating" :max="5" show-text />
        </el-form-item>
        <el-form-item label="评论内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评论内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交评论
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 评论列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>评论列表 ({{ comments.length }})</span>
          <el-button size="small" @click="fetchComments">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="comments" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="productId" label="商品 ID" width="80" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" />
        <el-table-column prop="createdAt" label="评论时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { commentApi, type Comment } from '../api/comment'

const loading = ref(false)
const comments = ref<Comment[]>([])
const submitting = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive({
  productId: 1,
  username: '',
  rating: 5,
  content: '',
})

const formRules: FormRules = {
  productId: [{ required: true, message: '请输入商品 ID', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 2, max: 500, message: '评论内容长度在 2 到 500 个字符', trigger: 'blur' },
  ],
}

/** 加载评论列表 */
async function fetchComments() {
  loading.value = true
  try {
    comments.value = await commentApi.getAll()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

/** 提交评论 */
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  submitting.value = true
  try {
    await commentApi.create({
      productId: formData.productId,
      username: formData.username,
      rating: formData.rating,
      content: formData.content,
    })
    ElMessage.success('评论提交成功')
    resetForm()
    await fetchComments()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.productId = 1
  formData.username = ''
  formData.rating = 5
  formData.content = ''
  formRef.value?.clearValidate()
}

/** 删除评论 */
async function handleDelete(comment: Comment) {
  try {
    await ElMessageBox.confirm(`确定要删除这条评论吗？`, '提示', {
      type: 'warning',
    })
    await commentApi.delete(comment.id!)
    ElMessage.success('删除成功')
    await fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.form-card {
  max-width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-rate {
  display: inline-flex;
}
</style>
