package io.zerows.epoch.basicore;

import com.hazelcast.shaded.com.zaxxer.hikari.HikariConfig;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.Database;
import io.r2mo.base.io.HStore;
import io.r2mo.typed.enums.DatabaseType;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.epoch.configuration.ZeroPlugins;
import io.zerows.epoch.metadata.MMComponent;

/**
 * 基本注释格式
 * <pre>
 *     1. ---> 💻️ 表示环境变量
 *        ---> 💻️ (R) 表示不带默认值, R = Required
 *     2. <--- 🔗 表示当前属性关联了当前文件中的某个节点
 *     3. 🔸 表示类型是特殊的 {@link Class}，通常需要通过 {@link SourceReflect} 进行实例化
 *     4. 配置优先级排序：🟢 > 🔵 > 🟡
 *     5. 🌷 表示 Vertx 中的原生 Options 的转换节点
 * </pre>
 * 完整数据格式介绍
 * -（云环境）vertx-boot.yml                                                  # {@link InPre}
 * <pre>
 * 🌐 云端配置 Nacos 连接，主要包含
 *    - 共享配置：r2mo-app-shared
 *    - 独享配置：r2mo-app-basic
 * vertx:                                                                   # {@link InPreVertx}
 *   config:                                                                # {@link InPreVertx.Config}
 *     import:
 *       - optional:nacos:r2mo-app-shared?refreshEnabled=true               # Nacos 共享配置
 *       - optional:nacos:${vertx.application.name}?refreshEnabled=true     # <--- 🔗 Nacos 独有配置
 *   application:                                                           # {@link YmApplication}
 *     name: ${Z_APP:r2mo-app-basic}                                        # ---> 💻️ Z_APP
 *
 * 🪐 访问 Nacos 的基本配置段，此处的配置段用于访问 Nacos 服务端，并且可直接从远程拉取配置信息
 * --- 分段文档配置 ---
 * vertx:                                                                   # {@link InPreVertx}
 *   cloud:                                                                 # {@link YmCloud}
 *     nacos:                                                               # {@link YmNacos}
 *       discovery:                                                         # {@link YmNacos.Discovery}
 *         server-addr: ${vertx.cloud.nacos.server-addr}                    # <--- 🔗 服务发现地址
 *         namespace: ${vertx.cloud.nacos.config.namespace}                 # <--- 🔗 服务发现名空间
 *       config:                                                            # {@link YmNacos.Config}
 *         server-addr: ${vertx.cloud.nacos.server-addr}                    # <--- 🔗 Nacos 基本地址
 *         namespace: ${R2MO_NACOS_NS_APP}                                  # ---> 💻️ (R) R2MO_NACOS_NS_APP, 应用名空间
 *         prefix: ${vertx.application.name}                                # <--- 🔗 Nacos 配置前缀
 *         file-extension: yaml                                             # Nacos 配置文件格式
 *       server-addr: ${R2MO_NACOS_ADDR:localhost:8848}                     # ---> 💻️ R2MO_NACOS_ADDR, Nacos 地址
 *       username: ${R2MO_NACOS_USER:nacos}                                 # ---> 💻️ Nacos 用户名
 *       password: ${R2MO_NACOS_PASS:nacos}                                 # ---> 💻️ Nacos 密码
 *       name: ${vertx.application.name}                                    # <--- 🔗 Nacos 名称
 * </pre>
 * ------------------------------------------------------------------------------------------------------------------------
 * -（本地环境）vertx.yml                                                     # {@link YmConfiguration}
 * <pre>
 * # =====> ⚗️ 启动配置
 * # 启动过程主要是定制 launcher，在没有高级设置干预的情况下可以使用自定义的启动器来开启容器
 * boot:                                                                    # {@link YmBoot}
 *   launcher:                                                              # 🔸 启动器
 *   pre:                                                                   # {@link MMComponent}
 *     component:                                                           # 🔸 容器启动完成后的环境预处理器（一定是容器创建完成）
 *     config:
 *   on:                                                                    # {@link MMComponent}
 *     component:                                                           # 🔸 配合 start 方法的生命周期插件
 *     config:
 *   off:                                                                   # {@link MMComponent}
 *     component:                                                           # 🔸 配合 stop 方法的生命周期插件
 *     config:
 *   run:                                                                   # {@link MMComponent}
 *     component:                                                           # 🔸 配合 restart / refresh 方法的生命周期插件
 *     config:
 *
 * # =====> ⚡️ 服务器专用配置
 * server:                                                                  # {@link YmServer}
 *   port:                                                                  # ---> 💻️ Z_API_PORT, 端口号
 *   address:                                                               # ---> 💻️ Z_API_HOST, 监听地址
 *   options:                                                               # 🌷 {@link HttpServerOptions}
 *     ssl:                                                                 # ---> 💻️ Z_API_SSL, 是否启用 SSL
 *     useAlpn:                                                             # 启用 ALPN 支持 HTTP/2
 *     keyStoreOptions:                                                     # 🌷 {@link KeyStoreOptions} / {@link HttpServerOptions#getKeyCertOptions()}
 *       type: jks                                                          # 密钥库类型 (JKS/PKCS12/PFX)
 *       path: keys/keystore-hotel.jks                                      # 密钥匙文件路径 / {@link HStore#pHome()} 为根路径
 *       password: "????"                                                   # 密钥匙访问密码
 *   session: 🔵                                                            # {@link YmSession}
 *     store-type:                                                          # 会话存储类型
 *     store-component:                                                     # 🔸 使用第三方存储时必须实现 {@link SessionStore}
 *     timeout:                                                             # 超时时间（分钟）
 *     options:                                                             # 扩展配置
 *     cookie:                                                              # {@link YmSession.Cookie}
 *       name:                                                              # Cookie 名称
 *       max-age:                                                           # Cookie 最大存活时间（秒）
 *   websocket:                                                             # {@link YmWebSocket}
 *     publish:                                                             # 启用发布通道（非安全模式）
 *     component:                                                           # 🔸 WebSocket 路由器
 *     config:
 *       webSocketSubProtocols:                                             # 支持的协议，其他配置参考 🌷 {@link HttpServerOptions}
 *       stomp:                                                             # {@link YmWebSocket.Stomp}
 *         port:                                                            # ---> 💻️ Z_SOCK_PORT, STOMP 端口
 *         secured:                                                         # 启用安全提供程序
 *         websocketBridge:                                                 # 启用 WebSocket 桥接
 *         websocketPath:                                                   # WebSocket 路径
 *         endpoint:                                                        # STOMP 端点
 *         bridge:                                                          # 桥接配置 🌷 {@link PermittedOptions}
 *         handler:                                                         # 🔸 STOMP 处理器
 *
 * # =====> 🧬 Vertx 实例配置（集群），如果是远程模式则和 vertx-boot.yml 三合一
 * vertx:                                                                   # {@link YmVertx} / {@link InPreVertx}
 *   application:                                                           # {@link YmApplication}
 *     name: ${Z_APP:r2mo-app-basic}                                        # ---> 💻️ Z_APP
 *   elasticsearch:                                                         # {@link YmElasticSearch}
 *     uris:
 *       -
 *     connection-timeout:
 *     socket-timeout:
 *     username:                                                            # 用户名
 *     password:                                                            # 密码
 *   neo4j:                                                                 # {@link YmNeo4j}
 *     uri:
 *     authentication:
 *       username:
 *       password:
 *       encrypted: true
 *   session: 🟢                                                            # {@link YmSession}
 *     store-type:                                                          # 会话存储类型
 *     store-component:                                                     # 🔸 使用第三方存储时必须实现 {@link SessionStore}
 *     timeout:                                                             # 超时时间（分钟）
 *     options:                                                             # 扩展配置
 *     cookie:                                                              # {@link YmSession.Cookie}
 *       name:                                                              # Cookie 名称
 *       max-age:                                                           # Cookie 最大存活时间（秒）
 *   config:                                                                # {@link YmVertx.Config} / {@link InPreVertx.Config}
 *     instance  :                                                          # {@link YmVertx.Instance[]}
 *       - name: instance-1                                                 # Vertx 名称，对应 ${vertx.application.name}，无指定则随机
 *         options:                                                         # 🌷 {@link VertxOptions}
 *         delivery: 🟢                                                     # ---> 参考 ${vertx.config.delivery}
 *         deployment: 🟢                                                   # ---> 参考 ${vertx.config.deployment}
 *         shared: 🟢                                                       # ---> 参考 ${vertx.config.shared}
 *     delivery: 🔵                                                         # {@link YmVertx.Delivery} / 🌷 {@link DeliveryOptions}
 *       timeout: 3000                                                      # 发送超时时间（毫秒）
 *       codecName:                                                         # 消息编解码器名称
 *       headers:
 *       localOnly:                                                         # 本地发送
 *       tracingPolicy:                                                     # 🌷 {@link TracingPolicy}
 *     deployment: 🔵                                                       # {@link YmVertx.Deployment}
 *       worker:                                                            # Worker 默认 / 🌷 {@link DeploymentOptions}
 *       workerOf:                                                          # Worker 特殊，每一个 class 对应一个 🌷 {@link DeploymentOptions}
 *         class-01:
 *         class-02:
 *         ...
 *       agent:                                                             # Agent 默认 / 🌷 {@link DeploymentOptions}
 *       agentOf:                                                           # Agent 特殊，每一个 class 对应一个 🌷 {@link DeploymentOptions}
 *         class-01:
 *         class-02:
 *         ...
 *     shared: 🔵                                                           # {@link Vertx#sharedData()}
 *   mvc:                                                                   # {@link YmMvc}
 *     freedom:                                                             # 是否自由格式，ZERO 标准是 data: ??? 的响应格式
 *     cors:                                                                # {@link CorsOptions}
 *       allowed-origins:                                                   # 允许的跨域地址
 *         - "???"
 *         - "???"
 *       allow-credentials:                                                 # 是否允许携带凭证
 *       allowed-methods:                                                   # 允许的请求方法
 *         - GET
 *         - POST
 *       allowed-headers:                                                   # 允许的请求头
 *       max-age:                                                           # 预检请求缓存时间（秒）
 *     resolver:                                                            # 已内置 application/json, ( WildCard ) 以及 application/octet=stream
 *       default:                                                           # 默认解析器
 *       application/xml:                                                   # 扩展 MIME 解析器 application/xml
 *       multipart/form-data:                                               # 扩展 MIME 解析器 multipart/form-data
 *   cluster:                                                               # {@link ClusterOptions}
 *     manager:                                                             # 🔸 集群管理器
 *     options:                                                             # 🌷 {@link ClusterManager}
 *   datasource:                                                            # {@link YmDataSource} / {@link Database}
 *     dynamic:                                                             # {@link YmDataSource.Dynamic}
 *       primary:                                                           # 主数据源名称
 *       strict:                                                            # 严格模式
 *       datasource:                                                        # Map 结构，name = {@link Database} 的数据库结构
 *         master: 🟢                                                       # 主库 {@link Database}
 *           url:                                                           # 数据库连接 URL
 *           username:                                                      # 数据库连接用户名
 *           password:                                                      # 数据库连接密码
 *           instance:                                                      # ---> 💻️ Z_DBS_INSTANCE
 *           driver-class-name:                                             # 数据库驱动
 *           category:                                                      # 数据库类型 {@link DatabaseType}, 默认 MYSQL
 *           hostname:                                                      # ---> 💻️ Z_DBS_HOST
 *           port:                                                          # ---> 💻️ Z_DBS_PORT
 *           options:                                                       # 其他特殊选项，如自动提交、事务配置等
 *         master-history: 🔵                                               # 历史库 {@link Database}
 *           url:                                                           # 数据库连接 URL
 *           username:                                                      # 数据库连接用户名
 *           password:                                                      # 数据库连接密码
 *           instance:                                                      # ---> 💻️ Z_DBH_INSTANCE
 *           driver-class-name:                                             # 数据库驱动
 *           category:                                                      # 数据库类型 {@link DatabaseType}, 默认 MYSQL
 *           hostname:                                                      # ---> 💻️ Z_DBH_HOST
 *           port:                                                          # ---> 💻️ Z_DBH_PORT
 *           options:                                                       # 其他特殊选项，如自动提交、事务配置等
 *         master-workflow: 🔵                                              # 工作流 库 {@link Database}
 *           url:                                                           # 数据库连接 URL
 *           username:                                                      # 数据库连接用户名
 *           password:                                                      # 数据库连接密码
 *           instance:                                                      # ---> 💻️ Z_DBW_INSTANCE
 *           driver-class-name:                                             # 数据库驱动
 *           category:                                                      # 数据库类型 {@link DatabaseType}, 默认 MYSQL
 *           hostname:                                                      # ---> 💻️ Z_DBW_HOST
 *           port:                                                          # ---> 💻️ Z_DBW_PORT
 *           options:                                                       # 其他特殊选项，如自动提交、事务配置等
 *     url:                                                                 # 略
 *     username:                                                            # ---> 💻️ Z_DB_USERNAME
 *     password:                                                            # ---> 💻️ Z_DB_PASSWORD
 *     instance:                                                            # 略
 *     driver-class-name:                                                   # 略
 *     category:                                                            # 略
 *     hostname:                                                            # 略
 *     port:                                                                # 略
 *     options:                                                             # 略
 *     hikari:                                                              # Hikari 连接池配置 🌷 {@link HikariConfig}
 *       minimum-idle:                                                      # 最小空闲连接数，以及其他
 *   data:                                                                  # {@link YmVertx.Data}
 *     redis:                                                               # {@link YmRedis}
 *       host:                                                              # ---> 💻️ R2MO_REDIS_HOST, Redis 主机
 *       port:                                                              # ---> 💻️ R2MO_REDIS_PORT, Redis 端口
 *       password:                                                          # ---> 💻️ R2MO_REDIS_PASSWORD, Redis 密码
 *       database:                                                          # ---> 💻️ R2MO_REDIS_DATABASE, Redis 数据库编号
 *       timeout:                                                           # 3000 连接超时时间（毫秒）
 *       endpoint:                                                          # 自动计算
 *
 * # =====> 🌀 Dubbo 配置，微服务通信
 * dubbo:                                                                   # {@link YmDubbo}
 *   application:                                                           # {@link YmDubbo.Application}
 *     name: ${vertx.application.name}                                      # <--- 🔗 Dubbo 应用名称
 *     qosPort: 33333                                                       # Dubbo QoS 端口
 *     serialize-check-status: DISABLE                                      # 启用序列化检查
 *   registry:                                                              # {@link YmDubbo.Registry}
 *     address: nacos://                                                    # 注册中心地址
 *     parameters:
 *       namespace: ${vertx.cloud.nacos.config.namespace}                   # <--- 🔗 Nacos 命名空间
 *       username: ${vertx.cloud.nacos.username}                            # <--- 🔗 Nacos 用户名
 *       password: ${vertx.cloud.nacos.password}                            # <--- 🔗 Nacos 密码
 *   protocol:                                                              # {@link YmDubbo.Protocol}
 *     name: dubbo                                                          # 协议名称
 *     port: 20880                                                          # 协议端口
 *   provider:
 *     serialization-security-check: false                                  # 启用序列化检查
 *   consumer:
 *     serialization-security-check: false                                  # 启用序列化检查
 *
 * # =====> 🛠️ 应用专用配置
 * app:                                                                     # {@link YmApp}
 *   id:                                                                    # ---> 💻️ Z_APP
 *   tenant:                                                                # ---> 💻️ Z_TENANT
 *   ns:                                                                    # ---> 💻️ Z_NS（动态建模专用名空间）
 *   data:                                                                  # 应用数据信息
 *   config:                                                                # 应用配置信息
 *
 * # ====> 🗄️ 存储专用配置
 * storage:                                                                 # {@link YmStorage}
 *   home:                                                                  # 存储根路径
 *   type:                                                                  # 存储类型
 *   provider:                                                              # 🔸 存储提供者
 *   options:                                                               # 存储配置
 *
 * # ====> 🧩 Request / Response 执行专用插件配置（插件用于当前应用）
 * plugins:                                                                 # {@link ZeroPlugins}
 *   [class1]:
 *     options1-1:
 *     options1-2:
 *
 * # ====> 📜 日志专用配置
 * logging:                                                                 # {@link YmLogging}
 *   level:
 *     [package-name]: INFO / WARN / ERROR / DEBUG                          # 日志记录
 *   file:
 *   logback:
 *
 * 🎱 （扩展）以下是动态配置----------------------------------------------------------------------------
 * # 📌 =====> 历史记录配置
 * trash:
 *   keepDay:
 *
 * # 📌 =====> Excel 配置
 * excel:
 *   pen:                                                                   # 🔸 皮肤渲染器
 *   temp:                                                                  # 临时文件目录
 *   tenant:                                                                # 租户初始化专用映射文件
 *   mapping:                                                               # 扩展映射
 *
 * # 📌 =====> 短信平台
 * sms:
 *   aliyun:
 *     access_id:
 *     access_secret:
 *     sign_name:
 *     tpl:
 *       [TPL1]: ???
 *       [TPL2]: ???
 *       [TPL3]: ???
 *
 * # 📌 =====> Flyway 数据库版本控制
 * flyway:                                                                  # 同 Spring
 *
 * # 📌 =====> Swagger
 * swagger-ui:                                                              # 同 springdoc.swagger-ui
 *
 * # 📌 =====> 任务系统
 * job:
 *   store:                                                                 # {@link MMComponent}
 *     component:                                                           # 🔸 存储组件
 *     config:
 *   client:                                                                # {@link MMComponent}
 *     component:                                                           # 🔸 客户端控制
 *     config:
 *   interval:                                                              # {@link MMComponent}
 *     component:                                                           # 🔸 调度组件
 *     config:
 *
 * # 📌 =====> 动态建模
 * metamodel:
 *   router:
 *     point:
 *       path: /api
 *       component:
 *     deployment:
 *       worker:
 *         instances: 64
 *       agent:
 *         instances: 32
 * </pre>
 */
public interface YmSpec {

    interface boot {
        String __ = "boot";

        String launcher = "launcher";

        interface pre {
            String __ = "pre";
            String component = "component";
            String config = "config";
        }

        interface on {
            String __ = "on";
            String component = "component";
            String config = "config";
        }

        interface off {
            String __ = "off";
            String component = "component";
            String config = "config";
        }

        interface run {
            String __ = "run";
            String component = "component";
            String config = "config";
        }
    }

    interface server {
        String __ = "server";
        String port = "port";
        String address = "address";

        interface options {
            String __ = "options";
            String ssl = "ssl";
            String useAlpn = "useAlpn";
            String clientAuth = "clientAuth";
            String idleTimeout = "idleTimeout";
            String compressionSupported = "compressionSupported";
            String maxWebSocketFrameSize = "maxWebSocketFrameSize";
            String maxWebSocketMessageSize = "maxWebSocketMessageSize";

            interface keyStoreOptions {
                String __ = "keyStoreOptions";
                String type = "type";
                String path = "path";
                String password = "password";
            }

            interface trustStoreOptions {
                String __ = "trustStoreOptions";
                String type = "type";
                String path = "path";
                String password = "password";
            }
        }

        interface websocket {
            String __ = "websocket";
            String component = "component";

            interface config {
                String __ = "config";
                String webSocketSubProtocols = "webSocketSubProtocols";

                interface stomp {
                    String __ = "stomp";
                    String port = "port";
                    String secured = "secured";
                    String endpoint = "endpoint";
                    String websocketBridge = "websocketBridge";
                    String websocketPath = "websocketPath";
                }
            }
        }
    }

    interface vertx {
        String __ = "vertx";

        interface cloud {
            String __ = "cloud";
            String server_addr = "server-addr";
            String username = "username";
            String password = "password";
            String name = "name";

            interface nacos {
                String __ = "nacos";
                String server_addr = "server-addr";
                String username = "username";
                String password = "password";
                String name = "name";

                interface discovery {
                    String __ = "discovery";
                    String server_addr = "server-addr";
                    String namespace = "namespace";
                }

                interface config {
                    String __ = "config";
                    String server_addr = "server-addr";
                    String namespace = "namespace";
                    String prefix = "prefix";
                    String file_extension = "file-extension";
                }
            }
        }

        interface config {
            String __ = "config";
            String import_ = "import";

            interface instance {
                String __ = "instance";
                String name = "name";

                interface options {
                    String __ = "options";
                    String maxEventLoopExecuteTime = "maxEventLoopExecuteTime";
                    String maxWorkerExecuteTime = "maxWorkerExecuteTime";
                    String preferNativeTransport = "preferNativeTransport";
                    String blockedThreadCheckInterval = "blockedThreadCheckInterval";
                    String eventLoopPoolSize = "eventLoopPoolSize";
                    String workerPoolSize = "workerPoolSize";
                    String internalBlockingPoolSize = "internalBlockingPoolSize";
                }
            }

            interface delivery {
                String __ = "delivery";
                String timeout = "timeout";
            }

            interface deployment {
                String __ = "deployment";
                String workerOf = "workerOf";
                String agentOf = "agentOf";

                interface worker {
                    String __ = "worker";
                    String instances = "instances";
                }

                interface agent {
                    String __ = "agent";
                    String instances = "instances";
                }
            }
        }

        interface application {
            String __ = "application";
            String name = "name";

        }

        interface mvc {
            String freedom = "freedom";

            interface cors {
                String __ = "cors";
                String allow_credentials = "allow-credentials";
                String allowed_origins = "allowed-origins";
                String allowed_methods = "allowed-methods";
                String allowed_headers = "allowed-headers";
                String max_age = "max-age";
            }

            interface resolver {
                String __ = "resolver";
            }
        }

        interface elasticsearch {
            String uris = "uris";
            String connection_timeout = "connection-timeout";
            String socket_timeout = "socket-timeout";
            String username = "username";
            String password = "password";
        }

        interface neo4j {
            String uri = "uri";

            interface authentication {
                String username = "username";
                String password = "password";
                String encrypted = "encrypted";
            }
        }

        interface session {
            String store_type = "store-type";
            String store_component = "store-component";
            String timeout = "timeout";             // 分钟

            interface cookie {
                String name = "name";
                String max_age = "max-age";
            }

            interface options {
                // 扩展配置
            }
        }

        interface cluster {
            String __ = "cluster";
            String manager = "manager";

            interface options {
                String __ = "options";
                String clusterPublicHost = "clusterPublicHost";
                String clusterPublicPort = "clusterPublicPort";
            }
        }

        interface datasource {
            String __ = "datasource";
            String dynamic = "dynamic";
            String url = "url";
            String username = "username";
            String password = "password";
            String driver_class_name = "driver-class-name";

            interface hikari {
                String __ = "hikari";
                String minimum_idle = "minimum-idle";
                String maximum_pool_size = "maximum-pool-size";
                String connection_timeout = "connection-timeout";
                String idle_timeout = "idle-timeout";
                String max_lifetime = "max-lifetime";
                String validation_timeout = "validation-timeout";
                String connection_test_query = "connection-test-query";
                String pool_name = "pool-name";
            }

            interface dynamic {
                String __ = "dynamic";
                String primary = "primary";
                String strict = "strict";

                interface _datasource {
                    String url = "url";
                    String username = "username";
                    String password = "password";
                    String instance = "instance";
                    String driver_class_name = "driver-class-name";
                }
            }
        }

        interface data {
            String __ = "data";

            interface redis {
                String __ = "redis";
                String host = "host";
                String port = "port";
                String password = "password";
                String database = "database";
                String timeout = "timeout";
                String endpoint = "endpoint";
            }
        }
    }

    interface dubbo {
        String __ = "dubbo";

        interface application {
            String __ = "application";
            String name = "name";
            String qosPort = "qosPort";
            String serialize_check_status = "serialize-check-status";
        }

        interface registry {
            String __ = "registry";
            String address = "address";

            interface parameters {
                String __ = "parameters";
                String namespace = "namespace";
                String username = "username";
                String password = "password";
            }
        }

        interface protocol {
            String __ = "protocol";
            String name = "name";
            String port = "port";
        }

        interface provider {
            String __ = "provider";
            String serialization_security_check = "serialization-security-check";
        }

        interface consumer {
            String __ = "consumer";
            String serialization_security_check = "serialization-security-check";
        }
    }

    interface app {
        String __ = "app";
        String id = "id";
        String tenant = "tenant";
        String ns = "ns";

        interface data {
            String __ = "data";
            String copyright = "copyright";
        }

        interface config {
            String __ = "config";
            String demo = "demo";
        }
    }

    interface storage {
        String __ = "storage";
    }

    interface excel {
        String __ = "excel";
        String pen = "pen";
        String temp = "temp";
        String tenant = "tenant";

        interface formula {
            String __ = "formula";
            String path = "path";
            String name = "name";
            String alias = "alias";
        }
    }

    interface flyway {
        String __ = "flyway";
        String locations = "locations";
        String schemas = "schemas";
        String table = "table";
        String baseline_on_migrate = "baseline-on-migrate";
        String clean_on_validation_error = "clean-on-validation-error";
        String enabled = "enabled";
        String encoding = "encoding";
        String group = "group";
        String out_of_order = "out-of-order";
        String skip_default_callbacks = "skip-default-callbacks";
        String skip_default_resolvers = "skip-default-resolvers";
        String sql_migration_prefix = "sql-migration-prefix";
        String sql_migration_separator = "sql-migration-separator";
        String sql_migration_suffixes = "sql-migration-suffixes";
        String validate_on_migrate = "validate-on-migrate";
        String placeholders = "placeholders";
        String placeholder_prefix = "placeholder-prefix";
        String placeholder_suffix = "placeholder-suffix";
        String resolvers = "resolvers";
        String callbacks = "callbacks";
        String target = "ofMain";
        String url = "url";
        String user = "user";
        String password = "password";
        String driver_class_name = "driver-class-name";
        String connect_retries = "connect-retries";
        String init_sql = "init-sql";
        String mixed = "mixed";
        String ignore_future_migrations = "ignore-future-migrations";
        String ignore_missing_migrations = "ignore-missing-migrations";
        String installed_by = "installed-by";
    }

    interface sms {
        String __ = "sms";

        interface aliyun {
            String __ = "aliyun";
            String domain = "domain";
            String region_id = "region-id";
            String access_id = "access-id";
            String access_secret = "access-secret";
            String sign_name = "sign-name";
            String tpl_name = "tpl-name";
        }
    }

    interface plugins {
        String __ = "plugins";
    }
}