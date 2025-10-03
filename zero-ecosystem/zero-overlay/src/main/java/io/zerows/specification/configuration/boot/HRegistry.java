package io.zerows.specification.configuration.boot;

import io.vertx.core.Future;
import io.zerows.platform.metadata.KTenement;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.access.app.HMod;
import io.zerows.specification.access.cloud.HTenant;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.security.identity.HOwner;
import io.zerows.specification.vital.HOI;

import java.util.Set;

/**
 * 「上下文环境」
 * 上下文环境在启动过程中会被直接初始化，执行统一归口，上下文初始化过程的执行流程
 * <pre><code>
 *     1. 托管启动
 *        - 直接执行平台级初始化，初始化信息包括：
 *          租户集合 / 应用集合 / 模块集合
 *          租户集合 - {@link HOwner}
 *          应用容器 - {@link HArk}
 *              应用集合 - {@link HApp}
 *              租户集合 - {@link HTenant}
 *          模块集合 - {@link HMod}
 *        - 应用集合 / 模块集合 的初始化过程中还会初始化对应的配置信息
 *        - 租户集合初始化过程中会直接初始化租户的资费、策略、计划信息
 *     2. 应用连接：应用存储连接初始化
 *        1）环境变量提取
 *        2）存储数据加载（X_APP / X_MODULE）以及 zero-battery 中的模块化配置
 *        3）和托管启动的上下文连接并对接
 *        4）请求流程中直让容器连接到上下文，可直接通过统一接口获取到上下文中的信息
 *        5）获取信息分两部分：跨 / 当前
 *     3. 组件连接：
 *        1）容器级路由管理器（和静态部分桥接）
 *        2）Endpoint对接（RESTful / WebSocket / Service-Bus）多模式下组件启动
 *        3）后台启动（任务、事件）
 *        4）扩展模块配置初始化（原 init 部分，位于 boot 配置中）
 *        5）Bundle接入（OSGI）
 * </code></pre>
 * 上下文环境中必定会存在的东西：
 * <pre><code>
 *     1. {@link HApp} 应用池
 *     2. {@link KTenement} 租户池
 * </code></pre>
 *
 * 上下文环境在启动时会执行初始化，初始化过程中加载应用对应的配置信息
 * <pre><code>
 *     1. {@link HApp} 应用加载，加载过程中有不同流程
 *        - 读取 X_APP 中的数据信息，加载 HApp 对象
 *        - 读取 configuration/app.yml 加载 HApp 对象（从文件中加载，Loader时会使用）
 *        - 读取某个 Git 空间中的应用配置
 *     2. 上下文环境只注册一次，之后的应用加载都是从上下文环境中获取
 * </code></pre>
 * 此接口还会替换原始的 {@see HET} 部分
 * <pre><code>
 *     1. 多环境模式的上下文注册可根据不同的注册实现 SPI 来完成
 *        - 基本环境使用 zero-ambient 中的 SPI 注册器
 *        - 云环境使用新的 SPI 注册器
 *     2. 注册之后的内容依赖 {@link HAmbient} 实现请求级别的连接，提取的每个 {@link HArk}
 *        中都包含了 {@link HOI} 部分，此部分依赖内置 {@see HAmbientContext}
 *        上下文环境处理
 *     3. 对接模式搜寻路径
 *        3.1. 名称转换
 *          sigma        = app name
 *          code         = app name
 *          appId        = app name
 *          appKey       = app name
 *          tenantId     = app name
 *          ns           = app name
 *          name         = app name
 *        3.2. 根据名称直接提取 {@link HArk}
 *     4. 四个核心对象
 *        - 应用容器内基本配置 {@link HArk#app()}
 *        - 应用容器内数据源配置 {@link HArk#database()}
 *        - 应用容器内拥有者配置 {@link HArk#owner()}
 * </code></pre>
 *
 * @author lang : 2023-06-05
 */
public interface HRegistry<T> {

    /**
     * 初始化上下文环境，生成当前上下文对应的 {@link HArk} 对象
     *
     * @param container 容器对象
     * @param config    配置信息
     *
     * @return {@link Set<HArk>}
     */
    Set<HArk> registry(T container, HConfig config);

    /**
     * 异步初始化上下文环境，生成当前上下文对应的 {@link HArk} 对象
     *
     * @param container 容器对象
     * @param config    配置信息
     *
     * @return {@link Future}
     */
    default Future<Set<HArk>> registryAsync(final T container, final HConfig config) {
        return Future.succeededFuture(this.registry(container, config));
    }

    /**
     * 注册器子接口，模块注册器，针对 boot: 启动流程中的 extension 部分注册静态模块
     * <pre><code>
     *     静态模块注册主要分成两类
     *     1）模块级注册，直接开启当前容器环境中的所有扩展模块核心功能，所有当前容器环境中运行的应用都会开启静态配置模块
     *        - 简单说一个 {@link Set<HArk>} 中所有的应用配置容器都会启用静态扩展模块
     *     2）应用级注册，不用应用会包含不同的应用级注册模块，这些模块只会在当前应用中开启，去掉原始配置中的 compile 节点
     *        而将 compile 阶段转换成 config 配置数据节点，针对不同的组件进行扩展配置以实现分离初始化
     * </code></pre>
     */
    interface Mod<T> {
        /**
         * 静态模块注册
         *
         * @param container 容器对象
         * @param ambient   环境对象
         *
         * @return {@link Boolean}
         */
        default Boolean configure(final T container, final HAmbient ambient) {
            return Boolean.TRUE;
        }

        /**
         * 异步静态模块注册
         *
         * @param container 容器对象
         * @param ambient   环境对象
         *
         * @return {@link Future}
         */
        default Future<Boolean> configureAsync(final T container, final HAmbient ambient) {
            return Future.succeededFuture(this.configure(container, ambient));
        }

        /**
         * （每个模块独立）初始化模块
         *
         * @param container 容器对象
         * @param ark       应用配置容器对象
         *
         * @return {@link Boolean}
         */
        default Boolean initialize(final T container, final HArk ark) {
            return Boolean.TRUE;
        }

        /**
         * 异步（每个模块独立）初始化模块
         *
         * @param container 容器对象
         * @param ark       应用配置容器对象
         *
         * @return {@link Future}
         */
        default Future<Boolean> initializeAsync(final T container, final HArk ark) {
            return Future.succeededFuture(this.initialize(container, ark));
        }
    }
}
