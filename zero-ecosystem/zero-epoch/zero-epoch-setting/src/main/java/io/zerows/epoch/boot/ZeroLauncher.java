package io.zerows.epoch.boot;

import io.r2mo.spi.SPI;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.configuration.ZeroConfigurer;
import io.zerows.platform.exception._11010Exception500BootIoMissing;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.spi.BootIo;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
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

    /** 🚀 实际的底层启动器，由 {@link BootIo#launcher()} 提供 */
    private final HLauncher<T> launcher;

    /** 🧱 启动前后配置器，负责绑定参数、生成/提取 {@link HConfig.HOn}、执行预初始化等 */
    @SuppressWarnings("rawtypes")
    private final ZeroConfigurer configurer;

    /**
     * 🛠️ 构造方法（私有）
     *
     * <p>完成如下工作：</p>
     * <ol>
     *   <li>通过 {@link SPI} 严格模式查找 {@link BootIo}；缺失则抛错。</li>
     *   <li>构造 {@link HEnergy} 并创建 {@link ZeroConfigurer}，绑定命令行参数。</li>
     *   <li>拉起 {@link HLauncher} 实例并记录日志。</li>
     * </ol>
     * 🧬 默认实现类：
     * <pre>
     *    - 启动器：{@link BootIo} / {@link ZeroBootIo}
     *    - 配置器：{@link ZeroConfigurer}
     * </pre>
     * 数据配置规范参考 {@link VertxYml}
     *
     * @param bootCls 启动入口类（通常为 Main/Boot 类） 📌
     * @param args    命令行参数（将作为 {@code "arguments"} 注入 {@link HConfig}） 🧵
     */
    private ZeroLauncher(final Class<?> bootCls, final String[] args) {
        /*
         * 🟤BOOT-001: 系统中直接查找 BootIo，此处调用了 HPI.findOverwrite 进行查找，查找过程中如果出现自定义
         *   的 BootIo 实现，则直接覆盖 ZeroBootIo 的实现，否则直接使用 ZeroBootIo 的实现作为默认实现处理，默认
         *   实现可启动一个最小的 Zero App 应用实例
         */
        final BootIo io = HPI.findOverwrite(BootIo.class);
        if (Objects.isNull(io)) {
            throw new _11010Exception500BootIoMissing(this.getClass());
        }




        /*
         * 🟤BOOT-002: 构造 HEnergy 对象，并创建 ZeroConfigurer 进行绑定，绑定过程中会根据配置类型对文件检查
         *   此处检查则考虑是否调用 HFS 的模式 -> 内置调用 HStore 从某个固定目录中提取配置信息，如果没有配置则考
         *   虑从 classpath 中提取配置。
         */
        final HEnergy energy = io.energy(bootCls, args);
        this.configurer = ZeroConfigurer.of(energy).bind(args);

        /*  启动器部分：获取底层 HLauncher 并记录其实现类  */
        this.launcher = io.launcher();
        log.info("[ ZERO ] 选择启动器: {}", this.launcher.getClass());
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
     * ▶️ 启动流程入口。
     *
     * <p>在内部完成 {@link HConfig.HOn} 的第一周期配置（环境连接、扫描、目录检查等）后，
     * 交由底层 {@link HLauncher} 启动；启动完成后：</p>
     *
     * <ol>
     *   <li>将命令行参数封装为 {@link JsonArray}，以 {@code "arguments"} 键注入到配置中。</li>
     *   <li>若存在配置对象，调用 {@link ZeroConfigurer#preExecute(Object, HConfig)} 执行容器就绪后的第一步初始化。</li>
     *   <li>调用外部 {@code consumer.accept(server, configuration)} 将控制权交还给调用方。</li>
     * </ol>
     *
     * <p><b>关于 {@code consumer}：</b>其语义等价于“启动完成后的穿透回调”，
     * 可直接拿到已经就绪的 <code>server</code> 与 <code>configuration</code> 进行业务初始化。</p>
     *
     * @param consumer 启动完成后的回调，参数依次为：<br/>
     *                 ・ <b>server</b>：已初始化的服务器实例（T）<br/>
     *                 ・ <b>configuration</b>：最终配置对象（CONFIG extends HConfig）<br/>
     * @param <CONFIG> 配置类型上界，必须实现 {@link HConfig}
     */
    @SuppressWarnings("unchecked")
    public <CONFIG extends HConfig> void start(final BiConsumer<T, CONFIG> consumer) {
        // 环境变量连接，执行环境变量初始化（如需在此阶段强制连接，可在 KConfigurer.environment() 中实现）
        // KConfigurer.environment();

        // 提取自配置的 HOn 组件，执行启动前的初始化（configure 第一周期已经完成）
        final HConfig.HOn on = this.configurer.onComponent();

        /*
         * 此处 {@link HOn} 已执行完 configure 的第一个周期
         * 直接使用 HOn 和 Consumer 配合完成启动流程
         *     1. 环境变量已连接
         *     2. 启动扫描已完成
         *     3. 文件目录已检查
         *     4. 可直接初始化 {@link T} 部分
         */
        this.launcher.start(on, server -> {

            final CONFIG configuration = Objects.isNull(on) ? null : (CONFIG) on.store();

            /*
             * 将参数部分传递到配置中，在 configuration 中构造：
             * arguments = JsonArray 结构
             */
            final JsonArray parameter = new JsonArray();
            final String[] arguments = on.args();
            Arrays.stream(arguments).forEach(parameter::add);

            // configuration 可能为 null（取决于 HOn 实现），判空后再写入与预执行
            if (Objects.nonNull(configuration)) {
                configuration.put("arguments", parameter);
                // Pre 1：针对容器初始化完成之后的第一步初始化流程
                this.configurer.preExecute(server, configuration);
            }

            /**
             * 此处是穿透效果，直接外层调用
             *     (server,config) -> {
             *         server -> 服务器引用（初始化好的框架部分）
             *         config -> 配置引用（初始化好的配置部分）
             *     }
             */
            consumer.accept(server, configuration);
        });
    }
}
