/**
 * 关于缓存的整体架构设计
 * <pre>
 *     1. SharedMap：Vertx 的内脏，用于器官协调、通信、状态同步
 *        - LocalMap：内存隔离的 Verticle 共享内存区
 *        - AsyncMap：集群内存共享，支持分布式场景，依赖不同的集群管理器，如 Hazelcast、Infinispan 等
 *     2. Redis/Caffeine：外脑和口袋，存储业务数据、加速读取
 * </pre>
 * 场景分析和相关说明
 * <pre>
 *  ID | 场景描述                 | 推荐组件              | 理由
 * ----+--------------------------+-----------------------+---------------------------------------------------------
 *  01 | Verticle 部署 ID 存储    | SharedMap (Local)     | 仅当前实例有效，无需网络开销，极快
 *  02 | 集群节点健康状态         | SharedMap (Async)     | 集群内广播和同步，强一致性需求低
 *  03 | EventBus 地址注册表      | SharedMap (Async)     | 必须集群可见，Vert.x 原生支持
 *  04 | 简单的原子计数器         | SharedMap (Counter)   | Vert.x 原生 API，轻量级
 *  05 | 动态配置共享             | SharedMap (Async)     | 配置中心下发后集群同步
 *  06 | 熔断器状态               | SharedMap (Local/Async)| 状态需实时共享或本地快速决策
 *  07 | 简单的分布式锁           | SharedMap (Lock)      | 基于集群管理器的锁，适合低频锁
 *  08 | WebSocket 连接映射       | SharedMap (Async)     | 记录用户ID与SocketID对应关系
 *  09 | 任务分发协调             | SharedMap (Async)     | 简单的任务队列或状态标记
 *  10 | 临时会话 (Sticky Session)| SharedMap (Local)     | 粘性会话场景，无需序列化
 *  11 | 用户热点数据 (Profile)   | Redis                 | 数据结构丰富，支持持久化，容量大
 *  12 | 商品详情页缓存           | Redis + Caffeine      | 多级缓存，抗高并发读
 *  13 | 购物车数据               | Redis                 | 需持久化，防止重启丢失，Hash结构适合
 *  14 | JWT 黑名单/吊销          | Redis                 | 设置过期时间 (TTL) 自动清理
 *  15 | API 响应缓存             | Caffeine (Local)      | 短期高频重复请求，本地缓存最快
 *  16 | 排行榜/积分榜            | Redis (ZSet)          | 原生支持排序，性能极高
 *  17 | 地理位置服务 (LBS)       | Redis (Geo)           | 原生支持 GEO 哈希计算和查询
 *  18 | 分布式锁 (高频/高可靠)   | Redis (Redlock)       | 比集群管理器锁更健壮，生态成熟
 *  19 | 布隆过滤器 (存在性校验)  | Redis                 | 内存占用小，适合海量数据去重
 *  20 | 消息队列/发布订阅        | Redis (Pub/Sub)       | 轻量级消息总线，非持久化消息
 *  21 | 验证码/临时Token         | Redis                 | 精确的 TTL 控制
 *  22 | 复杂查询结果集           | Caffeine              | 避免重复数据库复杂计算，本地缓存
 *  23 | 全局序列号生成           | Redis (Incr)          | 原子递增，全局唯一
 *  24 | 社交关系 (关注/粉丝)     | Redis (Set)           | 集合运算 (交集/并集) 方便
 *  25 | 访问频率限制 (Rate Limit)| Redis                 | 滑动窗口或令牌桶算法实现
 *  26 | 页面片段缓存             | Caffeine              | 静态化页面片段，减少模板渲染开销
 *  27 | 推荐系统特征数据         | Redis                 | 快速读取用户画像特征，低延迟
 *  28 | 实时股价/汇率            | SharedMap (Async)     | 高频更新，集群内所有节点需即时感知
 *  29 | 游戏房间状态             | Redis                 | 房间信息、玩家列表，需持久化和原子操作
 *  30 | 广告投放频次控制         | Redis                 | 严格控制展示次数，原子计数
 *  31 | 异步任务结果暂存         | Redis                 | 任务完成后存结果，供轮询或回调获取
 *  32 | 数据库主键预分配         | Redis (Incr)          | 批量获取ID段，减少数据库压力
 *  33 | 网站访问统计 (UV/PV)     | Redis (HyperLogLog)   | 极低内存占用统计海量基数
 *  34 | 热门搜索词               | Redis (ZSet)          | 实时统计搜索频率并排序
 *  35 | 优惠券库存扣减           | Redis (Lua)           | 原子操作扣减库存，防止超卖
 *  36 | 聊天室历史消息 (近期)    | Redis (List)          | 存储最近N条消息，新消息推入旧消息弹出
 *  37 | 用户在线状态             | Redis (Bitmap)        | 位图存储，极省空间，快速判断在线
 *  38 | 视频播放进度             | Redis                 | 记录用户播放位置，断点续播
 *  39 | 灰度发布规则             | SharedMap (Async)     | 规则简单且需全集群同步生效
 *  40 | 敏感词库缓存             | Caffeine              | 词库较大且变动少，本地加载提升过滤速度
 * </pre>
 */
package io.zerows.plugins.cache;