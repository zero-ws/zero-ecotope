package io.zerows.specification.configuration;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._80413Exception501NotImplement;
import io.zerows.platform.metadata.KPivot;
import io.zerows.specification.app.HArk;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 「启动器」专用接口
 * 用于主启动容器，主启动容器的类型为泛型，可以帮助系统启动完成，主容器生命周期很简单
 * <pre><code>
 *     - start：启动
 *     - stop：停止
 *     - refresh：刷新
 *     - restart：重启
 * </code></pre>
 * 示例代码通常如下：
 * <pre><code>
 *     HLauncher launcher = ...;
 *     launcher.start(server -> {
 *         // 真正的启动周期，之前可以针对 server 做相关调整以及配置
 *     });
 * </code></pre>
 *
 * 所有在启动之前和停止之后的动作都在实现类内部处理，而保证实现类的自由度，如：
 * <pre><code>
 *     1. 配置文件加载
 *     2. 准备类加载器
 *     3. 调用 Google Guice 启动IoC
 *     4. 准备文件和配置缓存
 *     5. 启动数据库连接池
 *     4. Metadata仓库基础配置
 * </code></pre>
 * 即：{@link HLauncher} 的每个行为的回调函数才是整个容器Ready之后刚刚创建的过程，内部实现全部放到实现类中。
 * <pre><code>
 *     1. 主容器种类
 *        - SM模式：OSGI Launcher -> Bundle ( Jetty )
 *        - Zero模式：Zero Launcher -> Vertx
 *        - Cloud模式：Cloud Launcher -> HAeon
 *     2. 辅助组件
 *        生命周期辅助组件为启动新规范，为了让启动器在每个生命周期多一层，且启动组件会
 *        跟随启动器的启动而启动，跟随启动器的停止而停止，所以此处的启动器需要提供一个
 *        完整的生命周期组件处理和启动相关的事情
 *        - on：{@link HConfig.HOn} 启动组件
 *        - off：{@link HConfig.HOff} 停止组件
 *        - run: {@link HConfig.HRun} 运行组件
 * </code></pre>
 *
 * @author lang
 */
public interface HLauncher<WebContainer> {
    /**
     * 启动服务器，由于此处在实现层会处理内置的配置模型，之所以使用 void 的返回值主要是
     * 考虑到启动过程中可能存在异步调用所以 void 可以保证通过 callback 的写法处理所有
     * 异步行为而不影响主逻辑，由于此类是入口类，所以此处根据最新的启动器规范进行修订，以保证
     *
     * @param energy 能量配置
     * @param server 服务器消费器（容器启动之后）
     */
    void start(HEnergy energy, Consumer<WebContainer> server);

    /**
     * 停止服务器，由于此处在实现层会处理内置的配置模型，所以使用回调模式 Consumer 来完
     * 成真正意义的启动，AOP这一层直接放到启动器停止后
     *
     * @param energy 能量配置
     * @param server 服务器消费器（容器停止之后）
     */
    void stop(HEnergy energy, Consumer<WebContainer> server);

    /**
     * 重启服务器
     *
     * @param energy 能量配置
     * @param server 服务器引用
     */
    default void restart(final HEnergy energy, final Consumer<WebContainer> server) {
        throw new _80413Exception501NotImplement();
    }

    /**
     * 刷新服务器（热部署模式）
     *
     * @param energy 能量配置
     * @param server 服务器引用
     */
    default void refresh(final HEnergy energy, final Consumer<WebContainer> server) {
        throw new _80413Exception501NotImplement();
    }

    /**
     * 节点模式的服务器专用
     *
     * @return 服务器存储
     */
    default ConcurrentMap<String, WebContainer> store() {
        return new ConcurrentHashMap<>();
    }

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
     *     1. {@see KLauncher} 结合现有的生命周期启动主容器
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
    interface Pre<WebContainer> {
        /**
         * 前置容器主体运行方法，会在容器启动之前触发，主要负责资源初始化
         */
        default Future<Boolean> waitAsync(final WebContainer container, final JsonObject options) {
            return Future.succeededFuture(Boolean.FALSE);
        }

        /**
         * 第二生命周期：直接在 {@link WebContainer} 启动之后，扩展模块启动之前处理
         * <pre><code>
         *     1. 跨应用级，直接传入 {@link Set} 类型的 {@link HArk} 应用配置
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
