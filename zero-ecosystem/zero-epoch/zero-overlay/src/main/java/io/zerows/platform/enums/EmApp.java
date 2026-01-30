package io.zerows.platform.enums;

/**
 * 🏢 @author lang : 2023-05-31
 */
public final class EmApp {
    private EmApp() {
    }

    /**
     * 🏷️ 应用专用维度 🚀
     * 🌐 原生云模式下，开始有「云租户」和「空间租户」的概念，开容器的条件
     * <pre>
     *     1. 🌍 维度一：语言容器：             X-Lang / language        （不同语言配置空间不同）
     *     2. 🏘️ 维度二：租户容器：             X-Tenant / tenant        （不同租户配置空间不同）
     *     3. 🛠️ 维度三：应用容器：             X-App / app              （不同应用配置空间不同）
     * </pre>
     * sigma 值游离于上述三个维度之外，sigma 代表统一标识符，sigma 的值可以和上述三个维度任意绑定。
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum Mode {
        /*
         * 🧩 「独立模式」
         * ⚡ 等价维度（现在使用的模式）
         * - sigma
         * - id
         * 🎯 整个Zero运行成一个独立应用，这个应用拥有唯一的应用ID，且这个应用的 sigma 也只有
         * 唯一的一个，sigma 的值也可标识应用，不开容器也可部署。
         *
         * 📊 1 sigma -- x tenant -- 1 id
         */
        CUBE,

        /*
         * 🏢 「多租户模式」（自动多应用）
         * ⚡ 等价维度
         * - sigma
         * - tenant
         * 🎯 此时，不同的租户会开不同的空间，Zero运行成一个平台应用，应用ID独立开容器，一个租户
         * 会包含一个或多个 app
         *
         * 📊 1 sigma -- 1 tenant -- n id
         */
        SPACE,

        /*
         * 🌌 「多层租户模式」（自动多应用）
         * ⚡ 等价维度（无）
         *
         * 📈 梯度维度
         * - 📌 维度一：sigma，代表统一标识符（云租户）
         * - 📌 维度二：tenant，代表二层租户（空间租户）
         * - 📌 维度三：id，一个租户会包含一个或多个 app
         *
         * 📊 1 sigma -- n tenant -- n id
         */
        GALAXY,

        /**
         * 🌍 前沿模式
         * 🎯 用于边界管理和网络隔离的前沿模式
         */
        FRONTIER,
    }

    /**
     * 🔄 「容器对接协议」
     * <hr/>
     * 📋 每种协议中只能包含一种组件，对于一个完整的 HLife 对接流程，对接协议必须全部满足，否则无法完成对接。
     * <pre><code>
     *     1. 🏃 应用所属运行端 {@see HNovae.HOne}
     *     2. 🐳 应用所属CRI {@see HCRI}
     *     3. 🏘️ 应用租户模式
     *        {@see HFrontier}
     *        {@see HGalaxy}
     *        {@see HSpace}
     *        🎯 此模式会直接调用内部的 `realm()` 方法，返回对应的租户边界
     *     4. 📦 关联部署计划 {@see HDeployment}
     *        - 🎯 目标容器 {@see HArk}
     *        - 📦 源容器 {@see HBackend}
     * </code></pre>
     *
     * @author lang : 2023-05-21
     */
    public enum Online {
        /**
         * 🏗️ 底座资源信息
         * 🎯 用于管理底层基础设施资源
         */
        ZONE,                       // HOne

        /**
         * 🐳 CRI信息
         * 🎯 用于管理容器运行时接口
         */
        CONTAINER,                  // HCri

        /**
         * 📦 部署计划关联
         * 🎯 用于管理应用部署计划
         */
        DEPLOYMENT,                 // HDeployment

        /**
         * 🎯 （目标）应用配置容器
         * 🎯 用于管理目标部署容器
         */
        DEPLOYMENT_TARGET,          // HArk

        /**
         * 📦 （源头）部署专用管理端
         * 🎯 用于管理部署源端
         */
        DEPLOYMENT_SOURCE,          // HAdmin
    }

    /**
     * 🏷️ 应用类型枚举 🚀
     * <pre>
     *     🎯 应用分类：
     *     - APPLICATION: 单机应用类型
     *     - GATEWAY: 微服务网关类型
     *     - SERVICE: 云端服务类型
     * </pre>
     *
     * @author lang : 2023-05-30
     */
    public enum Type {
        /**
         * 💻 单机应用
         * 🎯 传统的单体应用部署模式
         * 📋 适用于：
         * - 传统单体架构
         * - 简单应用场景
         * - 非分布式环境
         */
        APPLICATION,       // 单机应用

        /**
         * 🌐 微服务下的 Api Gateway
         * 🎯 微服务架构中的网关服务
         * 📋 适用于：
         * - 微服务架构
         * - API 网关服务
         * - 服务路由和负载均衡
         */
        GATEWAY,           // 微服务下的 Api Gateway

        /**
         * ☁️ 云端服务应用
         * 🎯 云原生环境下的服务应用
         * 📋 适用于：
         * - 云原生部署
         * - 容器化服务
         * - 云端微服务
         */
        SERVICE,           // 云端服务应用
    }

    /**
     * 🔄 应用生命周期枚举 🚀
     * 🎯 定义组件在不同阶段的状态和行为
     * <pre>
     *     📋 生命周期阶段：
     *     - PRE: 预处理阶段，组件初始化前的特殊处理
     *     - ON: 启动阶段，包含安装、解析、启动等过程
     *     - OFF: 停止阶段，包含停止、卸载等过程
     *     - RUN: 运行阶段，包含运行、更新、刷新等过程
     * </pre>
     *
     * @author lang : 2023-05-30
     */
    public enum LifeCycle {
        /**
         * 🎯 预处理阶段
         * 📋 组件初始化前的特殊处理阶段
         * 🎯 适用于需要在组件正式启动前进行预配置的场景
         */
        PRE,    // pre 组件，比较特殊的组件

        /**
         * 🎯 启动阶段
         * 📋 包含 install, resolved, start 等启动过程
         * 🎯 组件从安装到启动的完整生命周期
         */
        ON,     // install, resolved, start

        /**
         * 🎯 停止阶段
         * 📋 包含 stop, uninstall 等停止过程
         * 🎯 组件从停止到卸载的完整生命周期
         */
        OFF,    // stop, uninstall

        /**
         * 🎯 运行阶段
         * 📋 包含 run, update, refresh 等运行过程
         * 🎯 组件运行时的动态更新和刷新阶段
         */
        RUN;    // run, update, refresh

        /**
         * 🔄 根据名称获取生命周期枚举
         * 🎯 将字符串名称转换为对应的生命周期枚举值
         *
         * @param name 生命周期名称
         * @return 对应的生命周期枚举
         */
        public static LifeCycle from(final String name) {
            return LifeCycle.valueOf(name.toUpperCase());
        }
    }

    /**
     * 🔧 内置配置枚举 🚀
     * 🎯 定义系统内部核心配置组件
     * <pre>
     *     📋 内置组件分类：
     *     - SESSION: 会话管理配置
     *     - CORS: 跨域资源共享配置
     *     - SHARED_MAP: 共享映射配置
     *     - DEPLOYMENT: 部署管理配置
     *     - DELIVERY: 交付流程配置
     *     - SERVER: 服务器配置
     *     - CLUSTER: 集群配置
     *     - WEBSOCKET: WebSocket配置
     * </pre>
     *
     * @author lang : 2023-05-30
     */
    public enum Native {
        /**
         * 🔐 会话管理配置
         * 🎯 负责用户会话状态的管理和维护
         */
        SESSION,

        /**
         * 🌐 跨域资源共享配置
         * 🎯 处理跨域请求的安全策略配置
         */
        CORS,

        /**
         * 🗂️ 共享映射配置
         * 🎯 管理共享数据结构和映射关系配置
         */
        SHARED,

        /**
         * 📦 部署管理配置
         * 🎯 应用部署流程和策略的配置管理
         */
        DEPLOYMENT,

        /**
         * 🚚 交付流程配置
         * 🎯 应用交付和分发流程的配置管理
         */
        DELIVERY,

        /**
         * 🏢 服务器配置
         * 🎯 服务器运行时参数和行为的配置
         */
        SERVER,

        /**
         * 🖥️ 集群配置
         * 🎯 多节点集群环境的配置管理
         */
        CLUSTER,

        /**
         * 🔌 WebSocket配置
         * 🎯 实时双向通信协议的配置管理
         */
        WEBSOCKET,

        /**
         * 🛠️ Flyway配置
         * 🎯 管理数据库迁移和版本控制的配置
         */
        FLYWAY,

        /**
         * 🗄️ 数据库配置
         * 🎯 管理数据库连接和操作的配置
         */
        DATABASE,

        /**
         * 🧩 Redis配置
         * 🎯 管理Redis缓存和数据存储的配置
         */
        REDIS,

        /**
         * 🎬 Mvc配置
         * 🎯 管理模型-视图-控制器架构的配置
         */
        MVC,
    }
}