package io.zerows.extension.module.ambient.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.module.ambient.spi.RegistryExtension;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.extension.skeleton.metadata.MDModuleRegistry;
import io.zerows.platform.apps.KPivot;
import io.zerows.platform.apps.RegistryCommon;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * 此处 Ambient 模块本应该作为提供者存在，但是上层调用过程中还有一层预启动模型
 * <pre>
 *     1. {@link MDModuleRegistry} 会在 {@link HActor} 启动过程中被调用
 *        -> 内部调用 {@link KPivot} 对 {@link HAmbient} 进行初始化，而且会调用 SPI 核心扩展组件
 *           -> {@link HRegistry} 基础上下文初始化，实现类固定 {@link RegistryCommon}
 *           -> {@link HRegistry} 扩展上下文初始化，实现类根据 SPI 定义中处理，当前模块初始化使用 {@link RegistryExtension} 执行
 *     2. {@link MDModuleRegistry} 虽然每个模块会包含一个实例，但 {@link HAmbient} 的初始化只会执行一次
 *        -> {@link HAmbient} 中会包含多个 {@link HArk}
 *           name = {@link HArk}，内置 {@link HApp} 绑定关系
 *     3. 几层双设计模型
 *        -> Manager 和 Registry 的双设计模型
 *        -> 加载器 {@link KPivot} 和 {@link HAmbient} 的双设计模型
 *           -> {@link KPivot} 负责基础上下文、容器引用、扩展上下文
 *           -> {@link HAmbient} 负责多应用环境的基本上下文
 *        -> 动态运行时 {@link HArk} 和 静态编译时 {@link HApp} 的双设计模型
 *           -> {@link HArk} 负责运行时的应用环境
 *           -> {@link HApp} 负责编译时的应用配置
 * </pre>
 * 当前 {@link HActor} 不负责加载应用上下文环境，它所负责的主要是 Document Engine 的上下文初始化，也需要在 {@link HAmbient} 之后执行，所以
 * 即使：-ambient 模块是提供者，但它作为模块执行时主逻辑依旧是消费者。如果在微服务环境之下可直接更改 {@link MDModuleRegistry} 的实现逻辑来完成
 * 云环境的上下文对接，这样所有的模块都共享云环境中的核心上下文，而不需要单独更改。
 * <pre>
 *     配置块：
 *
 * </pre>
 *
 * @author lang : 2025-12-15
 */
@Actor(value = "extension", sequence = 207, configured = false)
@Slf4j
public class MDAmbientActor extends MDModuleActor {

    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        final AtConfig config = this.manager().config();

        /* 核心配置打印 */
        this.vLog("数据目录：\"{}\" @ Out-Of-Box", config.getDataFolder());
        /* 初始化组件 */
        this.vLog(" @/ \uD83E\uDE89 预处理组件：{}", config.getPrerequisite());
        this.vLog(" @/ \uD83E\uDE89 初始化组件：{}", config.getInitializer());
        this.vLog(" @/ \uD83E\uDE89 加载组件：{}", config.getLoader());

        return Future.succeededFuture(Boolean.TRUE);
    }

    // -------------------- Only Office 文档管理平台 ---------------------

    @Override
    @SuppressWarnings("unchecked")
    protected MDAmbientManager manager() {
        return MDAmbientManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<AtConfig> typeOfConfiguration() {
        return AtConfig.class;
    }
}
