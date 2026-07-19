# 🥭 青芒纪 (Mangrove)

> 粉丝应援社区平台 —— 记录每一次心动 · 定格每一份美好

一个面向粉丝群体的多功能应援社区平台，提供艺人资讯、多媒体资源、互动游戏、周边商城等一站式服务。

---

## 📋 目录

- [功能概览](#-功能概览)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [API 接口](#-api-接口)
- [管理后台](#-管理后台)
- [许可证](#-许可证)

---

## ✨ 功能概览

### 🏠 首页
- 精选照片轮播
- 芒果音乐播放器
- 动态日历提醒
- 舞台侧边栏快捷入口
- 照片轮播模块

### 🎤 艺人
- 艺人资料展示与编辑
- 艺人介绍区块化管理（传记、荣誉、作品等）
- 艺人封面与照片墙

### 📸 照片
- 照片分类标签筛选
- 精选照片轮播（Swiper）
- 分类网格展示
- 搜索功能

### 🎬 短视频
- 短视频瀑布流展示
- 分类筛选
- 随机播放
- 视频预览播放

### 🎥 长视频
- 长视频展厅模式
- 分类标签栏（粘性定位）
- 随机播放 / 换一个
- B站官方链接跳转
- 本地高清播放

### 🎵 音乐
- QQ 音乐深度集成
- 音乐专辑展示
- 每日推荐 / 热门歌单 / 新歌速递
- 歌曲分类筛选
- 内置播放器（播放、暂停、进度拖拽、音量控制、上一首/下一首）
- 歌曲收藏

### 🌳 芒果树
- 用户成长树系统
- 多阶段树木成长动画（树苗 → 小树 → 大树 → 金树）
- 浇水、施肥等互动操作
- 成长日记

### 🎨 创作
- 粉丝创作投稿
- 作品审核流程
- 作品分类展示

### 🛍️ 周边
- 周边商品展示
- 商品分类
- 购买流程

### 📅 行程
- 艺人行程日历
- 日程筛选与搜索
- 行程媒体管理

### 🎮 游戏
- 趣味互动游戏
- 答题闯关
- 排行榜与积分

### 💬 社区
- 粉丝交流社区
- 帖子发布与评论
- 互动点赞

### ✉️ 来信
- 粉丝来信系统
- 信件分类
- 公开信展示

### 🎂 生日
- 艺人生日专题
- 生日祝福聚合

### 🔧 管理后台
- **仪表盘**: 平台数据概览
- **用户管理**: 用户列表 / 角色分配
- **内容审核**: 作品 / 评论审核
- **艺人管理**: 资料 / 介绍区块编辑
- **照片管理**: 上传 / 分类 / 元数据
- **短视频管理**: 上传 / 分类
- **长视频管理**: 上传 / B站链接
- **音乐管理**: 曲目 / 专辑 / QQ音乐链接
- **周边管理**: 商品上架 / 分类
- **行程管理**: 日程发布 / 媒体关联
- **游戏管理**: 题目 / 关卡配置
- **来信管理**: 信件分类 / 审核
- **社区管理**: 帖子 / 评论
- **公告管理**: 发布平台公告
- **首页配置**: 首页模块动态配置
- **抽奖管理**: 抽奖活动配置
- **系统配置**: 全局参数设置

---

## 🛠 技术栈

### 前端

| 技术 | 用途 |
|------|------|
| **Vue 3** (Composition API) | 前端框架 |
| **Vue Router** | 路由管理 |
| **Vite** | 构建工具 |
| **Tailwind CSS v3** | 样式框架 |
| **Swiper** | 轮播组件 |
| **Lucide Icons** | 图标库 |
| **Axios / Fetch** | HTTP请求 |

### 后端

| 技术 | 用途 |
|------|------|
| **Java 17** | 开发语言 |
| **Spring Boot 3.x** | 应用框架 |
| **Spring Security** | 认证与授权 |
| **JPA / Hibernate** | ORM 持久化 |
| **MySQL** | 数据库 |
| **Maven** | 构建管理 |

---

## 📁 项目结构

```
Mangrove/
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── components/                # 公共组件
│   │   │   ├── AppNavbar.vue          # 导航栏
│   │   │   ├── AppFooter.vue          # 页脚
│   │   │   └── home/                  # 首页组件集
│   │   │       ├── CalendarBar.vue    # 日历条
│   │   │       ├── MusicPlayer.vue    # 音乐播放器
│   │   │       ├── PhotoCarousel.vue  # 照片轮播
│   │   │       └── StageSidebar.vue   # 舞台侧边栏
│   │   ├── views/                     # 页面视图
│   │   │   ├── HomePage.vue           # 首页
│   │   │   ├── PhotosPage.vue         # 照片
│   │   │   ├── VideosPage.vue         # 短视频
│   │   │   ├── LongVideosPage.vue     # 长视频
│   │   │   ├── MusicPage.vue          # 音乐
│   │   │   ├── ArtistsPage.vue        # 艺人
│   │   │   ├── WorksPage.vue          # 创作
│   │   │   ├── MerchandisePage.vue    # 周边
│   │   │   ├── SchedulePage.vue       # 行程
│   │   │   ├── GamesPage.vue          # 游戏
│   │   │   ├── CommunityPage.vue      # 社区
│   │   │   ├── LettersPage.vue        # 来信
│   │   │   ├── MyTreePage.vue         # 芒果树
│   │   │   ├── ProfilePage.vue        # 个人中心
│   │   │   ├── AuthPage.vue           # 登录注册
│   │   │   ├── BirthdayPage.vue       # 生日
│   │   │   └── admin/                 # 管理后台页面
│   │   ├── router/index.js            # 路由配置
│   │   ├── App.vue                    # 根组件
│   │   └── style.css                  # 全局样式
│   ├── public/assets/                 # 静态资源
│   └── package.json
│
├── src/                               # 后端项目
│   └── main/
│       ├── java/com/mangrove/
│       │   ├── config/                # 配置类（安全、数据源初始化等）
│       │   ├── controller/            # 控制器层
│       │   ├── service/               # 业务逻辑层
│       │   ├── repository/            # 数据访问层
│       │   ├── entity/                # 实体模型
│       │   └── dto/                   # 数据传输对象
│       └── resources/
│           ├── application.yml        # 应用配置
│           └── templates/             # 模板文件
│
├── docs/                              # 文档与SQL脚本
│   ├── mangrove-database.sql          # 数据库结构
│   ├── sql/                           # 增量迁移脚本
│   └── *.sql                          # 模块迁移脚本
│
├── pom.xml                            # Maven 配置
├── LICENSE                            # MIT 许可证
└── README.md                          # 本文档
```

---

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **Node.js 18+**
- **MySQL 8.0+**
- **Maven 3.8+**

### 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE mangrove CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入初始化脚本
mysql -u root -p mangrove < docs/mangrove-database.sql
```

### 后端启动

```bash
# 修改配置文件 src/main/resources/application.yml 中的数据库连接信息
# 然后编译运行
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173` 即可查看前端页面，后端默认运行在 `http://localhost:8080`。

---

## 🌐 API 接口

| 模块 | 端点 | 说明 |
|------|------|------|
| **认证** | `/api/auth/**` | 登录 / 注册 / 登出 |
| **文件** | `/api/files/**` | 文件上传 / 列表 / 元数据 |
| **艺人** | `/api/public/artists` | 艺人公开信息 |
| **音乐** | `/api/music/tracks` | 音乐曲目列表 |
| | `/api/music/albums` | 音乐专辑 |
| **长视频** | `/api/long-videos` | 长视频列表 |
| | `/api/long-video-categories` | 长视频分类 |
| **游戏** | `/api/games/**` | 游戏数据 |
| **行程** | `/api/schedules` | 行程安排 |
| **周边** | `/api/merchandise/**` | 周边商品 |
| **来信** | `/api/letters/**` | 粉丝来信 |
| **社区** | `/api/comments/**` | 评论互动 |
| **创作** | `/api/fan-works/**` | 粉丝创作 |
| **芒果树** | `/api/user-trees/**` | 用户树系统 |
| **公告** | `/api/public/announcements` | 平台公告 |
| **管理 APIs** | `/api/admin/**` | 管理后台接口 |

---

## 🖥 管理后台

访问 `/admin` 路径进入管理后台：

1. 使用管理员账号登录
2. 仪表盘查看平台数据总览
3. 左侧菜单导航至各管理模块
4. 支持内容审核、资源管理、用户角色分配等

---

## 📄 许可证

本项目基于 **MIT License** 开源 —— 详见 [LICENSE](./LICENSE) 文件。

版权所有 © 2026 [liguojing112](https://github.com/liguojing112)

---

> 🥭 **青芒纪** — 青涩的果实，终将长成参天大树。
