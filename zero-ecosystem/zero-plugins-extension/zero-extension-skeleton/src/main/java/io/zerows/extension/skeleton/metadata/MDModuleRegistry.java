package io.zerows.extension.skeleton.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.platform.metadata.KPivot;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 模块注册中心，用于原始流程中的 Electy 对比执行，当前扩展模块注册器只能在子类中调用
 * <pre>
 *     1. 父类是所有模块的主类，而此处有个悖论 {@link HArk} 必须依赖环境初始化流程，即 App 必须初始化之后才可以使用，这种场景下
 *        zero-exmodule-ambient 以及 zero-extension- 等扩展模块必须先启动，且 zero-exmodule-ambient 自身不用执行
 *        当前类中的方法
 *     2. 当前类主要用于 -exmodule 的业务模块初始化核心环境专用，所以其流程比较特殊
 *     3. 统一归口调用，由于近似于功能类，所以每个模块会包含一个 {@link MDModuleRegistry} 组件，如果是 OSGI 环境，可以在
 *        mid 上做相关处理，最终会形成一个统一的注册中心
 *
 *     ⚠️ / 特殊性：当前模块调用的特殊性在于 Provider 模块不会调用（如 zero-exmodule-ambient），但 Consumer 消费模块会调用
 *     此类进行环境注册，不仅如此，如果不是硬关联模式的模块在启动过程中也没有必要调用当前模块注册器
 *     - 🔗 硬关联：配置数据、系统数据依赖 App 才可以启动，必须访问：{@link HArk} / {@link HApp} 的完整模型才可以启动对应信息，如：
 *       - -mbsecore / 建模部分的动态模型
 *       - -mbseapi  / 接口部分的 Api 和 Job
 *       -           / 接口部分的服务组件 Service ( 内置 Service Component )
 *       - -ui       / 界面管理部分的界面基础模型
 *       简单说：非业务型依赖应用的部分启动会调用当前注册器完成环境对接
 *     - 🔗 软关联：只是运行数据依赖，可直接通过 HTTP 头中的 X-App-Id 完成，这种场景下不一定会调用此组件来执行环境对接
 * </pre>
 * 当前注册中心主要对接 AMS，简单说以下几个动作不在此处完成 ❌️：
 * <pre>
 *     1. {@link MDConfiguration} 统一模块加载配置环境
 *     2. 管理器中的特殊模块配置 -> 如 YmMetamodel 这种特定配置对接
 *     3. 特殊扩展或数据初始化
 * </pre>
 * 🚫 旧版移除部分：
 * <pre>
 *    init: 配置被移除，拥有了 {@link HActor} 模型之后，只要在 Maven 中引入即可，无需再设置是否加载
 *    boot: 配置被移除，同样被 {@link HActor} 模型代替
 *    🟢 -->
 *      简单说采用了 Spring Boot 中的 Starter 结构，只要在环境中引入了模块且这个模块中会包含 {@link HActor}，系统会自动将此 {@link HActor} 引入到模块的启动
 *      矩阵中完成启动流程的编排，这种模式下比以前的执行流程更加智能，且底层接口上的方法依旧会被调用，保持旧版的初始化流程不发生任何变化。
 * </pre>
 *
 * @author lang : 2025-12-22
 */
@Slf4j
public class MDModuleRegistry {
    private static final Cc<String, MDModuleRegistry> CC_REGISTRY = Cc.open();
    private final String mid;

    private MDModuleRegistry(final String mid) {
        this.mid = mid;
    }

    public static MDModuleRegistry of(final String mid) {
        return CC_REGISTRY.pick(() -> new MDModuleRegistry(mid), mid);
    }

    /**
     * 扩展启动流程：多租户环境
     * <pre>
     *     1. 多应用管理平台
     *     2. 多租户管理平台
     *     3. 多语言管理平台
     * </pre>
     * 此处需要追加一个流程就是 {@link HAmbient} 只会初始化一次，一旦初始化成功完成之后，就只执行读取操作，不会重复注册，通常
     * <pre>
     *     1. 单个应用启动时会执行初始化
     *     2. 单个服务（微服务）启动时会执行初始化
     * </pre>
     *
     * @param config   配置
     * @param vertxRef Vertx实例引用
     *
     * @return 多个应用容器环境
     */
    public Future<HAmbient> withAmbient(final HConfig config, final Vertx vertxRef) {
        final HAmbient ambient = KPivot.running();
        if (Objects.nonNull(ambient)) {
            return Future.succeededFuture(ambient);
        }

        final OCacheConfiguration store = OCacheConfiguration.of();
        final JsonObject configurationJ = store.configurationJ(this.mid);
        log.info("{} 配置数据加载：{}", KeConstant.K_PREFIX_BOOT, configurationJ.encode());

        final KPivot<Vertx> pivot = KPivot.of(vertxRef);
        return pivot.registryAsync(config)
            .compose(arkSet -> Future.succeededFuture(KPivot.running()));
    }
}
