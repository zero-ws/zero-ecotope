package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.boot.exception._40002Exception500UpClassInvalid;
import io.zerows.epoch.configuration.ZeroConfigurer;
import io.zerows.platform.ENV;
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

/**
 * 「启动管理器」KLauncher
 *
 * <p>一个开箱即用的轻量级启动封装：通过 {@link SPI} 自动发现 {@link BootIo}，
 * 构造 {@link HEnergy} 能量/配置上下文，完成预配置与启动过程，并在启动完成后以
 * {@link BiConsumer BiConsumer&lt;T, CONFIG&gt;} 的形式将“已初始化的服务器实例”和“配置对象”传递给调用方。</p>
 *
 * <h2>🚀 功能概览</h2>
 * <ul>
 *   <li>🧭 <b>SPI 驱动</b>：运行时通过 {@code SPI.findOne(BootIo.class)} 查找启动 I/O 组件。</li>
 *   <li>🔌 <b>零配置启动</b>：主函数可直接使用 {@link #create(Class, String[])} + {@link #start(BiConsumer)} 完成启动。</li>
 *   <li>🧱 <b>预配置阶段</b>：通过 {@link ZeroConfigurer} 绑定 {@link HEnergy} 与命令行参数，抽取 {@link HConfig.HOn}（启动前置 On 组件）。</li>
 *   <li>🧰 <b>可插拔生命周期</b>：调用 {@link ZeroConfigurer#preExecute(Object, HConfig)} 在容器就绪后执行第一步初始化。</li>
 *   <li>🧩 <b>类型安全泛型</b>：启动完成回调中可得到 <code>T</code>（服务端实例）与 <code>CONFIG extends HConfig</code>（配置）。</li>
 * </ul>
 *
 * <h2>🧠 生命周期（简述）</h2>
 * <ol>
 *   <li>🔍 SPI 查找 {@link BootIo}；若缺失抛出 {@link _11010Exception500BootIoMissing}。</li>
 *   <li>⚡ 从 {@link BootIo#energy(Class, String[])} 构建 {@link HEnergy}；创建并配置 {@link ZeroConfigurer}。</li>
 *   <li>🧪 提取 {@link HConfig.HOn}（启动扫描、文件目录检查、环境变量等工作已在 <i>configure 第一周期</i>完成）。</li>
 *   <li>🟢 通过 {@link BootIo#launcher()} 获取 {@link HLauncher} 并执行 {@link HLauncher#start(HConfig.HOn, java.util.function.Consumer)}。</li>
 *   <li>📦 构造启动参数 {@link JsonArray} 注入到 {@link HConfig}：键名为 {@code "arguments"}。</li>
 *   <li>🛠️ 执行 {@link ZeroConfigurer#preExecute(Object, HConfig)} 进行容器就绪后的首轮初始化。</li>
 *   <li>🤝 回调外部 {@link BiConsumer}，交付 <code>T server</code> 与 <code>CONFIG configuration</code>。</li>
 * </ol>
 *
 * <h2>🧷 单例与并发</h2>
 * <ul>
 *   <li>♻️ <b>单例</b>：内部使用静态 {@code INSTANCE} 保存启动器，仅在首次 {@link #create(Class, String[])} 时创建。</li>
 *   <li>🧵 <b>线程安全</b>：未做并发保护；如需多启动器并存或并发启动，请在外层保证串行化或改造单例策略。</li>
 * </ul>
 *
 * <h2>💡 使用示例</h2>
 * <pre>{@code
 * public static void main(String[] args) {
 *     KLauncher<MyServer> launcher = KLauncher.create(MyBoot.class, args);
 *     launcher.start((server, config) -> {
 *         // server: 已初始化好的服务器实例（T）
 *         // config: 已就绪的配置对象（CONFIG extends HConfig）
 *         // TODO: 你的业务启动逻辑
 *     });
 * }
 * }</pre>
 *
 * <h2>⚠️ 异常与日志</h2>
 * <ul>
 *   <li>❌ 未发现 {@link BootIo} 时会抛出 {@link _11010Exception500BootIoMissing}。</li>
 *   <li>📝 通过 {@see log} 输出启动组件相关日志（如发现的 {@link HLauncher} 实现类）。</li>
 * </ul>
 *
 * @param <T> 服务器/框架的核心实例类型（由底层 {@link HLauncher} 决定）
 *
 * @author lang
 * @since 2023-05-30
 */
@Slf4j
public class ZeroLauncher<T> {
    /** 🔒 单例实例（无并发保护，外层需确保仅初始化一次） */
    @SuppressWarnings("rawtypes")
    private static ZeroLauncher INSTANCE;
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
        HPI.monitorOf();


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
         */
        this.energy.initialize();
        // 提取自配置的 HOn 组件，执行启动前的初始化（configure 第一周期已经完成）


        /*
         * 🟤BOOT-009: 启动器的提取与启动
         */
        final HLauncher<T> launcher = this.boot.launcher();
        final Promise<T> before = Promise.promise();
        launcher.start(this.energy, vertx -> {
            /*
             * 🟤BOOT-010: 启动完成之后的基础回调，此时 Vertx 实例已创建
             */
            final HLauncher.Pre<T> launcherPre = this.boot.withPre();
            if (Objects.isNull(launcherPre)) {
                before.handle(Future.succeededFuture(vertx));
            } else {
                launcherPre.beforeAsync(vertx, new JsonObject()).onSuccess(res -> {
                    if (res) {
                        log.info("[ ZERO ] ( Pre ) 前置组件执行完成！");
                        before.handle(Future.succeededFuture(vertx));
                    }
                });
            }
        });


        /*
         * 🟤BOOT-011: 启动完成之后的配置回调
         */
        final HConfig.HOn<?> on = this.boot.whenOn();
        before.future().onSuccess(vertx -> {
            final CONFIG configuration = Objects.isNull(on) ? null : (CONFIG) on.store();
            consumer.accept(vertx, configuration);
        });
        // final HConfig.HOn on = this.configurer.onComponent();
        //        this.launcher.start(on, server -> {
        //
        //            final CONFIG configuration = Objects.isNull(on) ? null : (CONFIG) on.store();
        //
        //            /*
        //             * 将参数部分传递到配置中，在 configuration 中构造：
        //             * arguments = JsonArray 结构
        //             */
        //            final JsonArray parameter = new JsonArray();
        //            final String[] arguments = on.args();
        //            Arrays.stream(arguments).forEach(parameter::add);
        //
        //            // configuration 可能为 null（取决于 HOn 实现），判空后再写入与预执行
        //            if (Objects.nonNull(configuration)) {
        //                configuration.put("arguments", parameter);
        //                // Pre 1：针对容器初始化完成之后的第一步初始化流程
        //                this.configurer.preExecute(server, configuration);
        //            }
        //
        //            /**
        //             * 此处是穿透效果，直接外层调用
        //             *     (server,config) -> {
        //             *         server -> 服务器引用（初始化好的框架部分）
        //             *         config -> 配置引用（初始化好的配置部分）
        //             *     }
        //             */
        //            consumer.accept(server, configuration);
        //        });
    }
}
