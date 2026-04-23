package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.spec.exception._40002Exception500UpClassInvalid;
import io.zerows.platform.ENV;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.exception._11010Exception500BootIoMissing;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.spi.BootIo;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Slf4j
public class ZeroLauncher<T> {
    /**
     * 🔒 单例实例（无并发保护，外层需确保仅初始化一次）
     */
    private static final Cc<Class<?>, ZeroLauncher<?>> CC_LAUNCHER = Cc.open();
    private static final Cc<String, Pre<?>> CC_PRE = Cc.openThread();
    private static final Cc<String, Mod<?>> CC_MOD = Cc.openThread();
    private final HBoot boot;
    private final HEnergy energy;
    private Predicate<T> verifyFn;

    /**
     *
     * @param bootCls 启动入口类（通常为 Main/Boot 类） 📌
     * @param args    命令行参数（将作为 {@code "arguments"} 注入 {@link HConfig}） 🧵
     */
    private ZeroLauncher(final Class<?> bootCls, final String[] args) {
        /*
         * 🟤BOOT-001: 环境变量处理，访问 @PropertySource 处理对应的环境变量信息，保证环境变量的基础注入流程，针对
         *   核心环境变量的设置
         *   - 开发环境中 / @PropertySource 注解可绑定特定的环境变量辅助开发
         *   - 生产环境中 / 环境变量优先级高于配置文件，并且直接处理环境变量在 Docker 容器之外的注入流程
         */
        ENV.of().whenStart(bootCls);


        /*
         * 🟤BOOT-002: SPI 监控开启，用来监听 SPI 的接口完整信息，所有 SPI 在此处集中打印
         */
        HPI.vLog();


        /*
         * 🟤BOOT-003: 系统中直接查找 BootIo，此处调用了 HPI.findOverwrite 进行查找，查找过程中如果出现自定义
         *   的 BootIo 实现，则直接覆盖 ZeroBootIo 的实现，否则直接使用 ZeroBootIo 的实现作为默认实现处理，默认
         *   实现可启动一个最小的 Zero App 应用实例，此处的核心流程
         *   BootIo -->  HBoot
         *               -->  包含主启动器               -->  HLauncher ( 内置 @Up 的类信息可扫描 )
         *               -->  / 预处理启动器
         *               -->  / start 配置启动器
         *               -->  / stop 配置启动器
         *               -->  / restart 配置启动器
         *          -->  HEnergy
         *               -->  主启动配置信息（包含输入部分）
         *               -->  / 预处理启动配置
         *               -->  / start 启动配置
         *               -->  / stop 停止配置
         *               -->  / restart 重启配置
         */
        final BootIo io = HPI.findOverwrite(BootIo.class);
        if (Objects.isNull(io)) {
            throw new _11010Exception500BootIoMissing(this.getClass());
        }


        /*
         * 🟤BOOT-004: 通过 BootIo 构建 HBoot，HBoot 中管理了启动过程的所有生命周期，由于包含了 bootCls，可直接通过
         *   底层的 StoreSetting 提取到对应的配置 ID，此 ID 作为配置标识符，当前版本中
         *   - 程序入口        main         x 1
         *   - 配置数据        Setting      x 1
         *   - 启动程序        Launcher     x 1
         *   - 能量配置        Energy       x 1
         * 其中 Energy 和 Launcher 依赖 BootIo -> 接口提取
         *   - HBoot
         *   - HEnergy
         */
        this.boot = io.boot(bootCls);

        this.energy = io.energy(bootCls, args);


        // -40002 检查启动类是否被注解
        final Class<?> mainClass = this.boot.mainClass();
        Fn.jvmKo(!mainClass.isAnnotationPresent(Up.class), _40002Exception500UpClassInvalid.class, mainClass);
    }

    /**
     * 🧰 创建（或复用）启动器单例。
     *
     * <p>首次调用会以给定的 {@code bootCls} 与 {@code args} 进行初始化；后续调用将复用已有实例。</p>
     *
     * @param bootCls 启动入口类（用于 {@link BootIo#energy(Class, String[])}） 📌
     * @param args    命令行参数（将被注入配置） 🧵
     * @param <T>     服务器/框架的核心实例类型
     * @return 池化单例 {@link ZeroLauncher} 实例 🔁
     */
    @SuppressWarnings("unchecked")
    public static <T> ZeroLauncher<T> create(final Class<?> bootCls, final String[] args) {
        return (ZeroLauncher<T>) CC_LAUNCHER.pick(() -> new ZeroLauncher<>(bootCls, args), bootCls);
    }

    /**
     * 特殊启动器，带容器验证条件
     *
     * @param bootCls  启动入口类（用于 {@link BootIo#energy(Class, String[])}） 📌
     * @param args     命令行参数（将被注入配置） 🧵
     * @param <T>      服务器/框架的核心实例类型
     * @param verifyFn 容器验证函数
     * @return 池化单例 {@link ZeroLauncher} 实例 🔁
     */
    public static <T> ZeroLauncher<T> create(final Class<?> bootCls, final String[] args, final Predicate<T> verifyFn) {
        final ZeroLauncher<T> launcher = create(bootCls, args);
        launcher.verifyFn = verifyFn;
        return launcher;
    }

    /**
     * 按照如下方式启动
     * <pre>
     *     1. 启动之前执行 {@link HLauncher.Pre} -> 前序生命周期组件
     *     2. 启动过程中执行 {@link HLauncher} -> 主容器启动组件
     * </pre>
     *
     * @param consumer 启动完成后的回调
     * @param <CONFIG> 配置类型（必须继承自 {@link HConfig}）
     */
    @SuppressWarnings("unchecked")
    public <CONFIG extends HConfig> void start(final BiConsumer<T, CONFIG> consumer) {
        /*
         * 🟤BOOT-005: 先执行配置的完整初始化，调用 HEnergy 的 initialize 方法，执行过程中会处理核心环境的初始化
         *   - BOOT-006
         *   - BOOT-007
         *   - BOOT-008
         *   - BOOT-009
         */
        this.energy.initialize();
        // 提取自配置的 HOn 组件，执行启动前的初始化（configure 第一周期已经完成）


        /*
         * 🟤BOOT-010: 启动器的提取与启动
         */
        final HLauncher<T> launcher = this.boot.launcher();
        final Promise<T> before = Promise.promise();
        launcher.start(this.energy,
            /*
             * 🟤BOOT-011: 启动完成之后的基础回调，此时 Vertx 实例已创建
             */
            vertx -> this.startPreAsync(vertx)
                .compose(started -> started
                    ? Future.succeededFuture(vertx)
                    : Future.failedFuture(new _500ServerInternalException("[ ZERO ] 插件启动失败！")))
                .compose(container -> this.startModAsync(container)
                    .compose(started -> started
                        ? Future.succeededFuture(container)
                        : Future.failedFuture(new _500ServerInternalException("[ ZERO ] 扩展模块启动失败！"))))
                .onSuccess(before::tryComplete)
                .onFailure(error -> {
                    log.error("[ ZERO ] 启动链执行失败，入口 = {}", this.boot.mainClass().getName(), error);
                    this.cleanupContainer(vertx);
                    before.tryFail(error);
                })
        );


        /*
         * 🟤BOOT-014: 启动完成之后的配置回调
         */
        final HConfig.HOn<?> on = this.boot.whenOn();
        before.future().onSuccess(vertx -> {
            final CONFIG configuration = Objects.isNull(on) ? null : (CONFIG) on.store();
            try {
                consumer.accept(vertx, configuration);
            } catch (final Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        });
        before.future().onFailure(error ->
            log.error("[ ZERO ] 启动流程失败，consumer 未执行，入口 = {}", this.boot.mainClass().getName(), error)
        );
    }

    private boolean verifyContainer(final T container) {
        final boolean verified;
        if (Objects.nonNull(this.verifyFn)) {
            verified = this.verifyFn.test(container);
        } else {
            verified = true;
        }
        return verified;
    }

    @SuppressWarnings("unchecked")
    private Future<Boolean> startPreAsync(final T container) {
        if (!this.verifyContainer(container)) {
            return Future.failedFuture(new _500ServerInternalException("[ ZERO ] 容器验证函数验证失败，终止启动！"));
        }

        Objects.requireNonNull(container, "[ ZERO ] 启动容器不可以为 null.");
        HLauncher.Pre<T> launcherPre = this.boot.withPre();
        if (Objects.isNull(launcherPre)) {
            final String cacheKey = container.hashCode() + "@" + ZeroLauncher.class.getName();
            launcherPre = (HLauncher.Pre<T>) CC_PRE.pick(Pre::new, cacheKey);
        }
        final HConfig configurationPre = this.energy.boot(EmApp.LifeCycle.PRE);
        final JsonObject options = Objects.isNull(configurationPre) ? new JsonObject() : configurationPre.options();
        return launcherPre.waitAsync(container, options);
    }

    @SuppressWarnings("unchecked")
    private Future<Boolean> startModAsync(final T container) {
        Objects.requireNonNull(container, "[ ZERO ] 启动容器不可以为 null.");
        final String cacheKey = container.hashCode() + "@" + ZeroLauncher.class.getName();
        final HLauncher.Pre<T> launcherMod = (HLauncher.Pre<T>) CC_MOD.pick(Mod::new, cacheKey);
        return launcherMod.waitAsync(container, null);
    }

    private void cleanupContainer(final T container) {
        if (container instanceof final Vertx vertx) {
            vertx.close().onComplete(closed -> {
                if (closed.succeeded()) {
                    log.info("[ ZERO ] 启动失败后已关闭 Vert.x 容器");
                } else {
                    log.error("[ ZERO ] 启动失败后关闭 Vert.x 容器异常", closed.cause());
                }
            });
        }
    }

    private static class Pre<T> implements HLauncher.Pre<T> {
        @Override
        public Future<Boolean> waitAsync(final T container, final JsonObject options) {
            /*
             * 🟤BOOT-012 执行 HActor 的基础前置处理
             *   执行 < 0 的默认内置 HActor 组件
             *   如果是 > 0 的应该由 Zero Extension 框架执行而不是此处执行
             */
            return ZeroModule.of(container).startActor(sequence -> sequence < 0);
        }
    }

    private static class Mod<T> implements HLauncher.Pre<T> {
        @Override
        public Future<Boolean> waitAsync(final T container, final JsonObject options) {
            /*
             * 🟤BOOT-013 执行 HActor 的后置扩展模块
             *   执行 >= 0 的扩展 HActor 组件
             *   实际上代码本质很简单，但为了职责清晰，所以此处定义两个不同的类来处理
             */
            return ZeroModule.of(container).startActor(sequence -> sequence >= 0);
        }
    }
}
