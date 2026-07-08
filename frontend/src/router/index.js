// 12 个导航板块路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomePage.vue'),
    meta: { title: '首页', icon: 'home' }
  },
  {
    path: '/artists',
    redirect: '/artists/1',
    meta: { title: '小芒档案', icon: 'users' }
  },
  {
    path: '/photos',
    name: 'Photos',
    component: () => import('@/views/PhotosPage.vue'),
    meta: { title: '照片馆藏', icon: 'camera' }
  },
  {
    path: '/videos',
    name: 'Videos',
    component: () => import('@/views/VideosPage.vue'),
    meta: { title: '影像存档', icon: 'video' }
  },
  {
    path: '/music',
    name: 'Music',
    component: () => import('@/views/MusicPage.vue'),
    meta: { title: '音芒分享', icon: 'music' }
  },
  {
    path: '/merchandise',
    name: 'Merchandise',
    component: () => import('@/views/MerchandisePage.vue'),
    meta: { title: '芒园周边', icon: 'gift' }
  },
  {
    path: '/works',
    name: 'Works',
    component: () => import('@/views/WorksPage.vue'),
    meta: { title: '创作者殿堂', icon: 'pen-tool' }
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: () => import('@/views/SchedulePage.vue'),
    meta: { title: '行程日历', icon: 'calendar' }
  },
  {
    path: '/games',
    name: 'Games',
    component: () => import('@/views/GamesPage.vue'),
    meta: { title: '芒园小游戏', icon: 'gamepad-2' }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/CommunityPage.vue'),
    meta: { title: '音符互动', icon: 'message-circle' }
  },
  {
    path: '/tree',
    name: 'Tree',
    component: () => import('@/views/MyTreePage.vue'),
    meta: { title: '我的芒果树', icon: 'tree-pine' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfilePage.vue'),
    meta: { title: '个人中心', icon: 'user' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/AuthPage.vue'),
    meta: { title: '登录', hidden: true }
  },
  // ─── Admin ────────────────────────────────────
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/DashboardPage.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'artists',
        name: 'AdminArtists',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '艺人管理' },
      },
      {
        path: 'albums',
        name: 'AdminAlbums',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '相册管理' },
      },
      {
        path: 'content',
        name: 'AdminContent',
        component: () => import('@/views/admin/ContentManagePage.vue'),
        meta: { title: '内容管理' },
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManagePage.vue'),
        meta: { title: '分类管理' },
      },
      {
        path: 'media',
        name: 'AdminMedia',
        component: () => import('@/views/admin/ResourceManager.vue'),
        meta: { title: '媒体管理' },
      },
      {
        path: 'music',
        name: 'AdminMusic',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '音乐管理' },
      },
      {
        path: 'merchandise',
        name: 'AdminMerchandise',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '周边管理' },
      },
      {
        path: 'works',
        name: 'AdminWorks',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '作品管理' },
      },
      {
        path: 'schedules',
        name: 'AdminSchedules',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '行程管理' },
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '评论管理' },
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/GenericAdminPage.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'audit',
        name: 'AdminAudit',
        component: () => import('@/views/admin/AuditPage.vue'),
        meta: { title: '内容审核', requiresAuth: true, role: 'SUPER_ADMIN' },
      },
    ],
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLoginPage.vue'),
    meta: { title: '管理员登录', hidden: true },
  },

  {
    path: '/birthday',
    name: 'Birthday',
    component: () => import('@/views/BirthdayPage.vue'),
    meta: { title: '生日惊喜站', hidden: true }
  },
  {
    path: '/letters',
    name: 'Letters',
    component: () => import('@/views/LettersPage.vue'),
    meta: { title: '小芒来信', hidden: true }
  },
  {
    path: '/artists/:id',
    name: 'ArtistDetail',
    component: () => import('@/views/ArtistDetailPage.vue'),
    meta: { title: '艺人档案', hidden: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundPage.vue'),
    meta: { title: '404', hidden: true }
  }
]

export default routes
