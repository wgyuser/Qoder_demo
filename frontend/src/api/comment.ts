import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from '../utils/auth'

/** 后端统一返回格式 */
export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface Comment {
  id?: number
  productId: number
  username: string
  content: string
  rating: number
  createdAt?: string
}

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

/** 请求拦截器：自动添加 JWT Token */
api.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/** 响应拦截器：统一处理错误码 */
api.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult<unknown>
    if (result.code !== 200 && result.code !== 201) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      clearAuth()
      window.location.href = '/login'
      return Promise.reject(error)
    }
    const msg = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export const commentApi = {
  /** 获取所有评论 */
  getAll() {
    return api.get<ApiResult<Comment[]>>('/comments').then((res) => res.data.data)
  },

  /** 根据商品 ID 获取评论列表 */
  getByProductId(productId: number) {
    return api.get<ApiResult<Comment[]>>(`/comments/product/${productId}`).then((res) => res.data.data)
  },

  /** 根据 ID 获取评论 */
  getById(id: number) {
    return api.get<ApiResult<Comment>>(`/comments/${id}`).then((res) => res.data.data)
  },

  /** 创建评论 */
  create(comment: Omit<Comment, 'id' | 'createdAt'>) {
    return api.post<ApiResult<Comment>>('/comments', comment).then((res) => res.data.data)
  },

  /** 更新评论 */
  update(id: number, comment: Omit<Comment, 'id' | 'createdAt'>) {
    return api.put<ApiResult<Comment>>(`/comments/${id}`, comment).then((res) => res.data.data)
  },

  /** 删除评论 */
  delete(id: number) {
    return api.delete<ApiResult<void>>(`/comments/${id}`).then((res) => res.data.data)
  },
}
