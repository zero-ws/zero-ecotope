package io.zerows.specification.configuration.boot;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KLauncher;
import io.zerows.platform.metadata.KPivot;
import io.zerows.platform.metadata.KVarSet;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.configuration.HConfig;

import java.util.Set;

/**
 * 成熟度，处理环境变量专用的处理模型
 * - 主要用于环境变量的执行
 * - 以及处理环境的所有相关信息的执行
 * 替换原始的 ENV 部分的内容
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HMature {
    /* 环境变量加载，根据环境变量修订核心配置点，实现配置本身的处理 */
    JsonObject configure(JsonObject configJ, KVarSet set);

    /**
     * 注册器子接口，插件注册器，该注册器主要针对部分分流操作，执行同步执行处理，用于初始化
     * {@see Infix} 的核心功能插件系统，插件会分成几个部分
     * <pre><code>
     *     1. 容器启动后的内置插件部分
     *        如 MapInfix，JooqInfix 部分，这些内容依赖 {@link HConfig.HOn}
     *        组件提供核心插件配置（每个应用的实现有所区别）
     *     2. 容器启动之后的扩展插件部分
     *        此部分插件根据不同项目有所区别，依赖系统对整体环境的扫描结果
     * </code></pre>
     * 插件位置和主容器的配合：
     * <pre><code>
     *     1. {@link KLauncher} 结合现有的生命周期启动主容器
     *     2. （PlugIn）：主注册之前：主容器启动之后执行第一次加载：静态组件加载，此加载不依赖任何业务扩展
     *        {@link KPivot#registryAsync(HConfig)} 执行扩展注册
     *        - 第一：扩展注册有默认注册器（最小运行环境）
     *        - 第二：扩展注册连接 SPI 的方式实现扩展注册器的处理
     *     3. （PlugIn）：扩展配置之前：注册了基础容器之后
     *        - 执行扩展模块的连接、配置注册
     *        （PlugIn）：扩展初始化之前：扩展模块连接配置之后
     *        - 执行扩展模块初始化（业务级，配置已加载完成）
     * </code></pre>
     *
     * @param <WebContainer>
     */
    interface HPre<WebContainer> {
        /**
         * 第一生命周期：直接在 {@link WebContainer} 启动之后执行
         * <pre><code>
         *     1. 全局：所有运行在 {@link WebContainer} 中的扩展都会执行此流程
         *     2. 参数中不限制应用本身的配置，只有 {@link WebContainer}
         * </code></pre>
         *
         * @param container 容器对象
         *
         * @return {@link Boolean}
         */
        default Boolean beforeStart(final WebContainer container) {
            return this.beforeStart(container, new JsonObject());
        }

        default Boolean beforeStart(final WebContainer container, final JsonObject options) {
            return Boolean.TRUE;
        }

        /**
         * 第二生命周期：直接在 {@link WebContainer} 启动之后，扩展模块启动之前处理
         * <pre><code>
         *     1. 跨应用级，直接传入 {@link java.util.Set} 类型的 {@link HArk} 应用配置
         *     2. 会多一个核心参数 {@link Set} 应用配置集合对象
         * </code></pre>
         *
         * @param container 容器对象
         * @param arkSet    应用配置对象集合
         * @param config    应用配置对象
         *
         * @return {@link Boolean}
         */
        default Boolean beforeMod(final WebContainer container, final HConfig config, final Set<HArk> arkSet) {
            return Boolean.TRUE;
        }

        /**
         * {@link HPre#beforeStart} 的异步版本
         *
         * @param container 容器对象
         * @param arkSet    应用配置对象集合
         * @param config    应用配置对象
         *
         * @return {@link Future}
         */
        default Future<Boolean> beforeModAsync(final WebContainer container, final HConfig config, final Set<HArk> arkSet) {
            return Future.succeededFuture(this.beforeMod(container, config, arkSet));
        }

        /**
         * 第三生命周期：直接在 {@link WebContainer} 对应的扩展模块 {@link HRegistry.Mod} 的配置部分启动之后
         * 执行，此方法用于初始化扩展之前的核心操作
         *
         * @param container 容器对象
         * @param config    应用配置对象
         * @param arkSet    应用集合
         *
         * @return {@link Boolean}
         */
        default Boolean beforeInit(final WebContainer container, final HConfig config, final Set<HArk> arkSet) {
            return Boolean.TRUE;
        }

        /**
         * {@link HPre#beforeStart} 的异步版本
         *
         * @param container 容器对象
         * @param config    应用配置对象
         * @param arkSet    应用集合
         *
         * @return {@link Future}
         */
        default Future<Boolean> beforeInitAsync(final WebContainer container, final HConfig config, final Set<HArk> arkSet) {
            return Future.succeededFuture(this.beforeInit(container, config, arkSet));
        }
    }
}
