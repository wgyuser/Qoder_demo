import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from '../utils/auth'

/** 后端统一返回格式 */
export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface User {
  id?: number
  username: string
  email: string
  phone: string
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

export const userApi = {
  /** 获取所有用户 */
  getAll() {
    return api.get<ApiResult<User[]>>('/users').then((res) => res.data.data)
  },

  /** 根据 ID 获取用户 */
  getById(id: number) {
    return api.get<ApiResult<User>>(`/users/${id}`).then((res) => res.data.data)
  },

  /** 创建用户 */
  create(user: Omit<User, 'id'>) {
    return api.post<ApiResult<User>>('/users', user).then((res) => res.data.data)
  },

  /** 更新用户 */
  update(id: number, user: Omit<User, 'id'>) {
    return api.put<ApiResult<User>>(`/users/${id}`, user).then((res) => res.data.data)
  },

  /** 删除用户 */
  delete(id: number) {
    return api.delete<ApiResult<void>>(`/users/${id}`).then((res) => res.data.data)
  },
}
