package io.zerows.platform;

import io.zerows.spi.BootIo;

/**
 * 环境变量名称常量表，用于提供 Platform 级别所有支持的环境变量信息
 * 基本规范如下
 * <pre>
 *     1. 所有的开发专用配置都是 Z_DEV 前缀
 *     2. 其他配置均为 Z_ 前缀
 * </pre>
 * 环境变量启动过程中的加载流程
 * <pre>
 *     1. 查看系统中的环境变量 {@link System#getenv(String)/System#getProperty(String)}
 *     2. 查看 @Up 的注解中是否绑定了环境变量信息，主要是查验是否基于注解 PropertySource 进行加载
 *        - 不设置 classpath 前缀则使用当前路径加载
 *        - 设置 classpath 前缀则使用 classpath 路径加载
 *     3. 环境变量文件为
 *        - .env.development -> 开发模式
 *        - .env.production  -> 生产模式
 *        - .env.mockito     -> 测试模式
 *        三种不同模式对应的环境信息有所区别，除非特殊指定，否则默认就是开发环境
 * </pre>
 * 环境变量处理位于 {@link BootIo} 的构造之前，简单说整个容器环境的第一步骤就是加载环境变量，如果是生产环境，则可以考虑直接绑定操作系统环境变量，
 * 如此可实现 Docker 容器级别的启动流程，让整个容器启动更为轻量化。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface EnvironmentVariable {
    // 「Development」开发专用 ---------------------------------------
    /*
     * @Deprecated -> 这些变量后期可能都会被 @Slf4j 的日志级别所取代
     * 环境变量不出现在 vertx.yml 配置文件中，而是直接通过如下途径进行访问
     * 1. 直接通过环境变量访问器访问
     * 2. 通过 @Env 进行绑定（DI环境模式）
     * 3. 直接调用工具类：ENV -> 调用它的静态方法进行访问，访问过程会精确命中当前变量
     */
    @Deprecated(forRemoval = true)
    String DEV_IO = "Z_DEV_IO";                         // 查看底层IO日志
    @Deprecated(forRemoval = true)
    String DEV_JOOQ_COND = "Z_DEV_JOOQ_COND";           // 查看 jooq 查询条件日志（等价SQL日志）

    @Deprecated(forRemoval = true)
    String DEV_EXPR_BIND = "Z_DEV_EXPR_BIND";           // 表达式引擎专用日志打印
    @Deprecated(forRemoval = true)
    String DEV_EXCEL_RANGE = "Z_DEV_EXCEL_RANGE";       // Excel数据加载扫描日志
    @Deprecated(forRemoval = true)
    String DEV_JOB_BOOT = "Z_DEV_JOB_BOOT";             // 任务启动专用日志
    @Deprecated(forRemoval = true)
    String DEV_JVM_STACK = "Z_DEV_JVM_STACK";           // ex.printStackTrace() 异常日志
    @Deprecated(forRemoval = true)
    String DEV_WEB_URI = "Z_DEV_WEB_URI";               // URI路由检测状况
    @Deprecated(forRemoval = true)
    String DEV_DAO_BIND = "Z_DEV_DAO_BIND";             // CRUD的 Dao文件绑定
    @Deprecated(forRemoval = true)
    String DEV_AUTHORIZED = "Z_DEV_AUTHORIZED";         // 认证日志

    // 「容器级别」通用配置 ---------------------------------------

    /*
     * - 💻 Z_ENV  -> 当前环境变量，主要有三种：Production / Development / Mockito
     *   - Development：开发环境（默认）
     *   - Production：生产环境
     *   - Mockito：测试环境 -> 只有在单元测试中会访问此环境变量
     */


    String Z_ENV = "Z_ENV";
    /*
     *
     * - 💻 Z_NS     -> 一个UUID的值，标识当前 App / Service 所属的名空间
     *   - 如果是在 Cloud 环境，切换到云端之后，此处的 Z_NS 是必须的，它会对应到 Nacos 中的 Namespace
     *   - 如果是独立应用或独立服务，此处的 Z_NS 则在 MBSE 动态建模中作为所有模型的名空间来对待
     *   - 托管 App 时，每个 App 在 X_APP 中存储的 Namespace 则会分配它独立的名空间信息
     *   新规范之下此变量是必须配置的变量，它用来定义当前运行系统的归属
     *
     * - 💻 Z_TENANT -> 租户专用标识，当前系统中暂时没有使用，但开启第二管理端会启用租户标识，它会对应目前系统中的 X_TENANT 表数据
     *   实现针对租户的统一访问法则，新版所有表结构会自带 TENANT_ID 和 APP_ID 字段用于做标识
     *
     * - 💻 Z_APP    -> 应用标识符，位于 classpath 中的核心配置文件 vertx.yml 中进行基础配置，对应节点
     *   app:
     *     id:      xxx -> （UUID）唯一标识符
     *     tenant:  xxx -> 租户标识符
     *     name:    xxx -> app.zero.hotel
     *     sigma:   xxx -> 应用备用维度标识符
     *     data:
     *        xxx:  xxx -> 其他数据信息
     *     config:
     *        xxx:  xxx -> 其他配置信息
     */
    String Z_NS = "Z_NS";
    String Z_APP = "Z_APP";
    String Z_TENANT = "Z_TENANT";
    String Z_SIGMA = "Z_SIGMA";
    String Z_LANG = "Z_LANG";

    /*
     * Only Office 文档服务器专用
     */
    String Z_DOC_SECRET = "Z_DOC_SECRET";                 // 文档服务器的 secret（生成token）
    String Z_DOC_HOST = "Z_DOC_HOST";                     // 文档服务器的 host
    String Z_DOC_PORT = "Z_DOC_PORT";                     // 文档服务器的 port

    /*
     * 集成存储的路径专用设置
     */
    String SIS_STORE = "Z_SIS_STORE";                   // 集成服务中的存储
    String SIS_SCHEME = "Z_SIS_SCHEME";                 // 集成服务中的协议，如 ftp / https / 等等
    String SIS_HOST = "Z_SIS_HOST";                     // 集成服务中的主机
    String SIS_PORT = "Z_SIS_PORT";                     // 集成服务中的端口
    String SIS_USERNAME = "Z_SIS_USERNAME";             // 集成服务中的用户名
    String SIS_PASSWORD = "Z_SIS_PASSWORD";             // 集成服务中的密码
    // 「Production」生产专用 ---------------------------------------
    @Deprecated(forRemoval = true)
    String HED_COMPONENT = "Z_HED";                     // 外置 HED 模块
    @Deprecated(forRemoval = true)
    String HED_ENABLED = "Z_HED_ENABLED";               // 是否打开 HED 模块


    String CORS_DOMAIN = "Z_CORS_DOMAIN";               // 跨域配置（可支持多个，这个作为额外的添加）
    // RESTful 端口号/主机
    String API_PORT = "Z_API_PORT";
    String API_HOST = "Z_API_HOST";
    String API_SSL = "Z_API_SSL";

    // WebSocket 端口号/主机
    String SOCK_PORT = "Z_SOCK_PORT";
    String SOCK_HOST = "Z_SOCK_HOST";
    String SOCK_SSL = "Z_SOCK_SSL";

    // 数据库端口号/主机
    String DBS_PORT = "Z_DBS_PORT";
    String DBS_HOST = "Z_DBS_HOST";
    String DBS_INSTANCE = "Z_DBS_INSTANCE";

    // 工作流数据库端口号/主机
    String DBW_PORT = "Z_DBW_PORT";
    String DBW_HOST = "Z_DBW_HOST";
    String DBW_INSTANCE = "Z_DBW_INSTANCE";

    // 历史数据库端口号/主机
    String DBH_PORT = "Z_DBH_PORT";
    String DBH_HOST = "Z_DBH_HOST";
    String DBH_INSTANCE = "Z_DBH_INSTANCE";

    String DB_USERNAME = "Z_DB_USERNAME";
    String DB_PASSWORD = "Z_DB_PASSWORD";
    // 数据库在三个库中的账号和密码必须相同，此处三个库并非 App 的数据库，而是 Cloud 专用的三库

    // 「Cloud」云端专用 ---------------------------------------
    @Deprecated(forRemoval = true)
    String AEON_CLOUD = "AEON_CLOUD";
    @Deprecated(forRemoval = true)
    String AEON_APP = "AEON_APP";

    // 「Nacos」配置中心专用 ---------------------------------------
    /*
     * 当前运行应用程归属相关的基础环境变量信息，主要牵涉是否以独立应用或独立服务启动，其中最核心的基础变量如：
     * - 💻 Z_HOME -> 是否已配置，如果已配置则可以在生产环境中直接使用它作基础工作目录
     *   此环境变量直接对接已经配置完成的 R2MO_HOME 来实现开发和生产的一体化操作，它的作用：
     *   - 开启开发发布模式，发布过程中以此作工作目录
     *   - 开启全链路统一 Cloud 的目录设置，不论开启多少 instance 实例，都以此为根目录
     */
    String R2MO_HOME = "R2MO_HOME";
    String R2MO_NACOS_ADDR = "R2MO_NACOS_ADDR";                 // Nacos 主机
    String R2MO_NACOS_USERNAME = "R2MO_NACOS_USERNAME";         // Nacos 用户名
    String R2MO_NACOS_PASSWORD = "R2MO_NACOS_PASSWORD";         // Nacos 密码
    String R2MO_NS_CLOUD = "R2MO_NS_CLOUD";
    String R2MO_NS_APP = "R2MO_NS_APP";

    String[] NAMES = new String[]{
        // 通用配置变量
        R2MO_HOME, R2MO_NS_CLOUD, R2MO_NS_APP,
        R2MO_NACOS_ADDR, R2MO_NACOS_USERNAME, R2MO_NACOS_PASSWORD,
        Z_TENANT, Z_APP, Z_ENV, Z_SIGMA,
        Z_NS, Z_LANG,
        // 文档服务器变量
        Z_DOC_SECRET, Z_DOC_HOST, Z_DOC_PORT,
        // 集成存储变量
        SIS_STORE, SIS_SCHEME, SIS_HOST, SIS_PORT, SIS_USERNAME, SIS_PASSWORD,
        // 云端变量
        AEON_CLOUD, AEON_APP,
        // 应用一阶变量
        Z_NS, Z_APP, Z_LANG, Z_SIGMA,
        // 跨域
        CORS_DOMAIN,
        // RESTful
        API_HOST, API_PORT, API_SSL,
        // Sock
        SOCK_HOST, SOCK_PORT, SOCK_SSL,
        // DB Service
        DBS_HOST, DBS_PORT, DBS_INSTANCE,
        // DB Workflow
        DBW_HOST, DBW_PORT, DBW_INSTANCE,
        // DB History
        DBH_HOST, DBH_PORT, DBH_INSTANCE,
        // 数据库账号密码
        DB_USERNAME, DB_PASSWORD
    };
}
