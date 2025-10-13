package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.boot.exception._40002Exception500UpClassInvalid;
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

@Slf4j
public class ZeroLauncher<T> {
    /** 🔒 单例实例（无并发保护，外层需确保仅初始化一次） */
    @SuppressWarnings("rawtypes")
    private static ZeroLauncher INSTANCE;
    private static final Cc<String, Pre<?>> CC_PRE = Cc.openThread();
    private final HBoot boot;
    private final HEnergy energy;

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
     *
     * @return 单例的 {@link ZeroLauncher} 实例 🔁
     */
    @SuppressWarnings("unchecked")
    public static <T> ZeroLauncher<T> create(final Class<?> bootCls, final String[] args) {
        if (INSTANCE == null) {
            INSTANCE = new ZeroLauncher<>(bootCls, args);
        }
        return (ZeroLauncher<T>) INSTANCE;
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
             *   - BOOT-012:
             */
            vertx -> this.beforeAsync(vertx).onSuccess(done -> {
                if (done) {
                    log.info("[ ZERO ] ( Pre ) 前置组件执行完成！");
                    before.complete(vertx);
                }
            })
        );


        /*
         * 🟤BOOT-013: 启动完成之后的配置回调
         */
        final HConfig.HOn<?> on = this.boot.whenOn();
        before.future().onSuccess(vertx -> {
            final CONFIG configuration = Objects.isNull(on) ? null : (CONFIG) on.store();
            consumer.accept(vertx, configuration);
        });
    }

    @SuppressWarnings("unchecked")
    private Future<Boolean> beforeAsync(final T container) {
        Objects.requireNonNull(container, "[ ZERO ] 启动容器不可以为 null.");
        HLauncher.Pre<T> launcherPre = this.boot.withPre();
        if (Objects.isNull(launcherPre)) {
            final String cacheKey = container.hashCode() + "@" + ZeroLauncher.class.getName();
            launcherPre = (HLauncher.Pre<T>) CC_PRE.pick(Pre::new, cacheKey);
        }
        final HConfig configurationPre = this.energy.boot(EmApp.LifeCycle.PRE);
        final JsonObject options = Objects.isNull(configurationPre) ? new JsonObject() : configurationPre.options();
        return launcherPre.beforeAsync(container, options);
    }

    /**
     * @author lang : 2025-10-13
     */
    private static class Pre<T> implements HLauncher.Pre<T> {
        @Override
        public Future<Boolean> beforeAsync(final T container, final JsonObject options) {
            return Future.succeededFuture(container)
                /*
                 * 🟤BOOT-011 执行 HActor 的基础前置处理
                 *   执行 < 0 的默认内置 HActor 组件
                 */
                .compose(containerWeb -> ZeroModule.of(container).startActor(sequence -> sequence < 0));
        }
    }
}
