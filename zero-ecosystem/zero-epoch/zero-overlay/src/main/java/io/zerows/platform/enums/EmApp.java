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
     * </pre>
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
}