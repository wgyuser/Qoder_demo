const TOKEN_KEY = 'auth_token'
const USER_KEY = 'auth_user'

export interface AuthUser {
  username: string
  role: string
}

/** 获取存储的 JWT 令牌 */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/** 存储 JWT 令牌 */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 移除 JWT 令牌 */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

/** 获取当前登录用户信息 */
export function getAuthUser(): AuthUser | null {
  const data = localStorage.getItem(USER_KEY)
  return data ? JSON.parse(data) : null
}

/** 存储当前登录用户信息 */
export function setAuthUser(user: AuthUser): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/** 移除用户信息 */
export function removeAuthUser(): void {
  localStorage.removeItem(USER_KEY)
}

/** 是否已登录 */
export function isAuthenticated(): boolean {
  return !!getToken()
}

/** 清除所有认证信息 */
export function clearAuth(): void {
  removeToken()
  removeAuthUser()
}
