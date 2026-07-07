import { createRouter, createWebHistory } from 'vue-router'
import UserList from '../views/UserList.vue'
import CommentList from '../views/CommentList.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import { isAuthenticated } from '../utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'UserList',
      component: UserList,
      meta: { requiresAuth: true },
    },
    {
      path: '/comments',
      name: 'CommentList',
      component: CommentList,
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
    },
  ],
})

/** 全局前置守卫：登录状态检查 */
router.beforeEach((to, _from, next) => {
  const loggedIn = isAuthenticated()

  if (to.meta.requiresAuth && !loggedIn) {
    // 需要认证但未登录，跳转到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if ((to.path === '/login' || to.path === '/register') && loggedIn) {
    // 已登录用户访问登录/注册页，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router
