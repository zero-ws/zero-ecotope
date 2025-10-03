package io.zerows.epoch.boot;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.corpus.cloud.LogCloud;
import io.zerows.management.OZeroStore;
import io.zerows.epoch.program.Ut;
import io.zerows.platform.metadata.KLauncher;
import io.zerows.platform.metadata.KPivot;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.boot.HMature;
import io.zerows.specification.configuration.boot.HRegistry;
import io.zerows.spi.BootIo;
import io.zerows.support.FnBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Arcane:神秘的，秘密的（被替换的原始类名）
 * Zero新版启动器，新的初始化流程发生了变动，直接更改成了如下流程
 * <pre><code>
 *     1. {@link KLauncher} 构造
 *        内部流程：
 *        - {@link BootIo} SPI 连接
 *        - 初始化环境变量
 *        - 提取 {@link HConfig.HOn} 构造配置扫描器
 *        - 执行 {@link HConfig.HOn#configure} 初始化 {@link HConfig}
 *     2. 完成 WebServer 的创建过程（此处 WebServer 是主框架的容器实例）
 *        - Zero 中 Vertx 实例
 *        - OSGI 中 Framework 实例
 * </code></pre>
 * 上述步骤完成后调用此类中的方法，做静态启动流程（此处启动流程为共享流程，依旧处于初始化阶段）
 *
 * @author lang : 2023-06-10
 */
class ElectyEnroll {
    private static final String MSG_EXT_COMPONENT = "Extension components initialized {0}";

    private static final List<HRegistry.Mod<Vertx>> REGISTERS = new Vector<>();

    /**
     * 「Vertx启动后」扩展流程二
     * <pre><code>
     * 必须在Extension启动完成后执行，且作为非标准化的扩展启动部分
     * - 1. 多应用管理平台
     * - 2. 多租户管理平台
     * - 3. 多语言管理平台
     *
     * </code></pre>
     */
    static Future<Set<HArk>> registryStart(final Vertx vertx, final HConfig config) {
        final KPivot<Vertx> pivot = KPivot.of(vertx);
        return pivot.registryAsync(config);
    }

    static Handler<AsyncResult<Boolean>> registryEnd(final Actuator actuator) {
        return res -> {
            if (res.succeeded()) {
                final boolean registered = res.result();
                if (registered) {
                    Fn.jvmAt(actuator);
                } else {
                    // 异常退出
                    System.exit(1);
                }
            } else {
                final Throwable ex = res.cause();
                if (Objects.nonNull(ex)) {
                    ex.printStackTrace();
                }
                // 异常退出
                System.exit(1);
            }
        };
    }

    /*
     * 「Vertx启动后」扩展流程三 / 配置
     */
    static Future<Boolean> registryAmbient(final Vertx vertx, final HConfig config, final Set<HArk> arkSet) {
        return registryAsync(config, pre -> pre.beforeModAsync(vertx, config, arkSet), () -> {
            final HAmbient ambient = KPivot.running();
            final List<HRegistry.Mod<Vertx>> registers = registerComponent();
            final List<Future<Boolean>> futures = new ArrayList<>();
            registers.stream()
                .map(register -> register.configureAsync(vertx, ambient))
                .forEach(futures::add);
            return FnBase.combineB(futures);
        });
    }

    /*
     * 「Vertx启动后」扩展流程三 / 初始化
     */
    static Future<Boolean> registryArk(final Vertx vertx, final HConfig config, final Set<HArk> arkSet) {
        return registryAsync(config, pre -> pre.beforeInitAsync(vertx, config, arkSet), () -> {
            final List<Future<Boolean>> futures = new ArrayList<>();
            /*
             * 双模式矩阵初始化展开成笛卡尔积
             * Set<HArk> -> HArk
             * List<HRegistry.Mod<Vertx>> -> HRegistry.Mod<Vertx>
             */
            final List<HRegistry.Mod<Vertx>> registers = registerComponent();
            arkSet.forEach(ark -> registers.stream()
                .map(register -> register.initializeAsync(vertx, ark))
                .forEach(futures::add));
            return FnBase.combineB(futures);
        });
    }

    private static Future<Boolean> registryAsync(
        final HConfig config,
        final Function<HMature.HPre<Vertx>, Future<Boolean>> runnerPre, final Supplier<Future<Boolean>> runner) {
        final Class<?> implCls = config.pre();
        if (Objects.isNull(implCls)) {
            return runner.get();
        }
        final HMature.HPre<Vertx> pre = Ut.singleton(implCls);
        if (Objects.isNull(pre)) {
            return runner.get();
        }
        return runnerPre.apply(pre).compose(nil -> runner.get());
    }

    /**
     * 提取模块构造器，提取过程中针对静态变量执行同步和初始化，每个程序只执行一次
     *
     * @return 模块构造器列表
     */
    private static List<HRegistry.Mod<Vertx>> registerComponent() {
        if (REGISTERS.isEmpty()) {
            final JsonObject initConfig = OZeroStore.option(YmlCore.init.__KEY);
            LogCloud.LOG.Env.info(ElectyEnroll.class, MSG_EXT_COMPONENT, initConfig.encode());
            // 1. nativeComponent first
            final JsonArray bridges = Ut.valueJArray(initConfig, YmlCore.init.CONFIGURE);
            // 2. 针对每个组件的统一初始化
            Ut.itJArray(bridges)
                .map(json -> {
                    final String className = json.getString(YmlCore.init.configure.COMPONENT);
                    return Ut.clazz(className, null);
                })
                .filter(Objects::nonNull)
                .filter(clazz -> Ut.isImplement(clazz, HRegistry.Mod.class))
                .forEach(instance -> REGISTERS.add(Ut.singleton(instance)));
        }
        // 3. 组件收齐，执行初始化
        return REGISTERS;
    }
}
