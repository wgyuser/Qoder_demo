import axios from 'axios'
import { ElMessage } from 'element-plus'

/** 后端统一返回格式 */
export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface AuthResponse {
  token: string
  username: string
  role: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

const authApi = axios.create({
  baseURL: '/api/auth',
  timeout: 10000,
})

/** 响应拦截器：统一处理错误码 */
authApi.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult<unknown>
    if (result.code !== 200 && result.code !== 201) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
    return response
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export const auth = {
  /** 用户登录 */
  login(data: LoginRequest) {
    return authApi.post<ApiResult<AuthResponse>>('/login', data).then((res) => res.data.data)
  },

  /** 用户注册 */
  register(data: RegisterRequest) {
    return authApi.post<ApiResult<AuthResponse>>('/register', data).then((res) => res.data.data)
  },
}
