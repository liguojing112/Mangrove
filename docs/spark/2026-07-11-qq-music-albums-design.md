# QQ 音乐外链与照片专辑设计

## 背景与目标

Candice's Mango Ode是为单一艺人“小芒”制作的站点。音乐页目前直接读取上传的 MP3 文件并在站内播放。为降低未经授权分发音乐文件的风险，同时保留已获授权或可合法使用的本地音频，音乐资源需要同时支持本地 MP3 和 QQ 音乐官方链接。

本次还要在音乐页筛选标签中增加“专辑”，用于展示照片专辑。歌曲、专辑封面和专辑照片继续统一从超管后台的“资源管理”页面维护。

本站不解析、代理或下载 QQ 音乐音频，不自动抓取 QQ 音乐的歌曲名、歌词或封面。管理员只使用自有或已获授权的展示素材。外链方式能减少本站直接分发音频的风险，但不构成法律免责。

参考：

- [腾讯音乐娱乐服务协议](https://www.tencentmusic.com/zh-cn/protocol.html)
- [腾讯音乐娱乐权利声明](https://www.tencentmusic.com/zh-cn/right.html)

## 数据模型

### music_track

- `id`
- `title`：歌曲名，必填
- `category`：单曲、舞台或翻唱，必填
- `local_audio_url`：本站 MP3 地址，可空
- `qq_music_url`：QQ 音乐链接，可空
- `cover_url`：管理员上传的自有或授权封面，可空
- `duration_seconds`：可选；本地音频可由前端读取，QQ-only 歌曲不强制填写
- `sort_order`
- `play_count`
- `status`
- `created_at`、`updated_at`

约束：`local_audio_url` 与 `qq_music_url` 至少填写一个。项目只服务“小芒”，不保存歌手字段；前台统一通过常量 `SITE_ARTIST_NAME = '小芒'` 展示。

### music_photo_album

- `id`
- `title`：专辑名称，必填
- `description`：可空
- `cover_url`：独立上传或选择的封面，必填
- `sort_order`
- `status`
- `created_at`、`updated_at`

### music_album_photo

- `id`
- `album_id`
- `image_url`
- `caption`：可空
- `sort_order`
- `created_at`

删除专辑时同步删除照片记录，但是否删除物理图片文件沿用资源管理现有规则，避免误删被其他页面引用的文件。

## 后端接口

公开读取接口：

- `GET /api/music/tracks`：按状态、分类和排序返回歌曲
- `POST /api/music/tracks/{id}/play`：站内播放开始或 QQ 链接点击时增加播放次数
- `GET /api/music/albums`：返回专辑卡片列表
- `GET /api/music/albums/{id}`：返回专辑与已排序照片

超管接口：

- `POST /api/admin/music/tracks`
- `PUT /api/admin/music/tracks/{id}`
- `DELETE /api/admin/music/tracks/{id}`
- `POST /api/admin/music/albums`
- `PUT /api/admin/music/albums/{id}`
- `DELETE /api/admin/music/albums/{id}`
- `POST /api/admin/music/albums/{id}/photos`
- `PUT /api/admin/music/albums/{id}/photos/order`
- `DELETE /api/admin/music/albums/{id}/photos/{photoId}`

文件上传继续复用 `/api/files/upload`。上传接口只负责保存文件，音乐接口负责保存歌曲、专辑及关联关系。

## 前台体验

音乐页筛选标签为：`全部 / 单曲 / 舞台 / 翻唱 / 专辑`。

歌曲列表规则：

- 有 MP3：显示站内播放按钮，可使用底部播放器。
- 有 QQ 音乐链接：显示带外链图标的“QQ 音乐”按钮。
- 两者都有：同时显示两个入口。
- QQ-only 歌曲不显示 `--:--`，时长区域显示“QQ 音乐”。
- QQ 音乐通过新窗口打开，并使用 `target="_blank"` 与 `rel="noopener noreferrer"`。
- 歌手固定显示“小芒”。

点击“专辑”后，歌曲列表替换为专辑封面网格。点击专辑卡片进入 `/music/albums/:id`，展示名称、封面、简介和照片墙。

顶部推荐轮播继续存在，其每日推荐、热门歌单和新歌速递改为读取 `music_track`。热门歌单使用后端 `play_count`，不再只依赖浏览器本地记录。

## 超管资源管理

资源管理页增加三个子视图：

1. **资源文件**：保留现有照片、视频和音频上传管理。
2. **音乐歌曲**：创建和编辑歌曲；填写歌曲名、分类、QQ 音乐链接，上传或选择 MP3 与封面。MP3 和 QQ 链接至少有一个。
3. **音乐专辑**：创建专辑、上传独立封面、批量上传照片、删除照片和调整顺序。

QQ 音乐歌曲信息全部手动维护，不调用非官方解析接口。

## 校验与错误处理

- QQ 音乐链接仅允许 HTTPS，并校验主机为 `y.qq.com` 或其子域名。
- 后端重复执行同样校验，不能只依赖前端。
- MP3 和 QQ 链接均为空时拒绝保存，并给出明确提示。
- QQ 链接打开失败不影响页面其他内容，链接按钮保持可复制。
- 专辑不存在或已下架时显示普通空状态，并提供返回音乐页入口。
- 图片或封面加载失败时使用本站默认占位图。
- 接口失败时保留当前列表，显示简短错误提示，不清空已加载内容。

## 迁移与兼容

增加一次性迁移逻辑，将当前上传目录中的音频文件及 `file_meta` 名称、分类、排序转换为 `music_track`：

- `display_name` -> `title`
- `category` -> `category`
- 文件 URL -> `local_audio_url`
- `seq_no` -> `sort_order`

迁移不删除原文件和 `file_meta`。完成并核对后，音乐页切换为新接口；照片和视频页面继续使用现有文件接口。

## 测试范围

- MP3-only、QQ-only、双来源歌曲的保存、展示和操作
- 非 QQ 域名、非 HTTPS 链接及空来源校验
- 分类筛选和“专辑”视图切换
- 底部播放器只响应本地 MP3
- QQ 链接新窗口打开及安全属性
- 专辑创建、封面、批量照片、排序和删除
- 现有 MP3 迁移后名称、分类和 URL 保持一致
- 桌面和移动端列表、专辑网格及播放器不重叠

## 不在本次范围

- 解析或嵌入 QQ 音乐播放器
- 抓取 QQ 音乐封面、歌词、时长和歌曲信息
- 多艺人管理
- 评论、收藏和付费音乐能力
- 删除现有上传文件或重构照片、视频资源模型
