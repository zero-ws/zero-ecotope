package io.zerows.epoch;

import io.vertx.core.Vertx;
import io.zerows.cosmic.bootstrap.StubLinear;
import io.zerows.epoch.boot.Electy;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.configuration.ZeroEnvironment;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.metadata.KRunner;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.spi.BootIo;

/**
 * 标准启动器，直接启动 Vertx 实例处理 Zero 相关的业务逻辑
 */
public class VertxApplication {
    /**
     * 统一入口，不同对象的职责说明
     * <pre>
     *     - {@link HBoot} / 启动规范，定义了应用程序启动的主体规范信息
     *       - {@link HEnergy} 负责的配置核心对象（已初始化完成）
     *       - {@link HLauncher} 负责的启动器对象（未启动）
     * </pre>
     * {@link ZeroLauncher} 内置成员对象说明
     * <pre>
     *     - {@see ZeroLauncher#INSTANCE}
     *       / 单例对象
     *     - {@see ZeroLauncher#launcher}
     *       / 从 {@link BootIo} 中提取的 {@link HLauncher} 对象
     *     - {@see ZeroLauncher#configurer}
     *       / 通过 {@link BootIo} 中提取的 {@link HEnergy} 来构造的内置对象
     * </pre>
     * 此处的 configurer 是一个调和类，用于协调 HEnergy 与 HLauncher 之间的关系，HEnergy 本身并非直接参与启动
     * 流程，而是通过 configurer 来间接影响启动过程。启动模式如下:
     * <pre>
     *     - App 独立应用启动
     *     - Service 微服务启动
     *     - Extension 独立应用启动（多出更多插件加载）
     * </pre>
     * 新版环境变量的特殊性处理，不再依赖环境变量来做核心启动，只有 Service 微服务启动模式下会依赖 Nacos 相关变量
     * 来对接配置中心，其他两种模式都不依赖环境变量
     * <pre>
     *     1. {@link EnvironmentVariable#R2MO_NS_APP} -> nacos 中已经配置好的 APP 专用名空间
     *     2. {@link EnvironmentVariable#R2MO_NS_CLOUD} -> nacos 中已经配置好的 CLOUD 专用名空间
     *     Nacos 相关变量
     *     - {@link EnvironmentVariable#R2MO_NACOS_ADDR} -> nacos 服务器地址
     *     - {@link EnvironmentVariable#R2MO_NACOS_PORT} -> nacos 服务器端口
     *     - {@link EnvironmentVariable#R2MO_NACOS_USERNAME} -> nacos 登录用户名
     *     - {@link EnvironmentVariable#R2MO_NACOS_PASSWORD} -> nacos 登录密码
     * </pre>
     *
     * @param clazz 启动主类
     * @param args  启动参数
     */
    public static void run(final Class<?> clazz, final String... args) {
        /*
         * MOMO-001: 环境变量初始化，并给出最终打印结果（打印只考虑当前应用支持的环境变量）
         */
        ZeroEnvironment.of().whenStart(clazz);
        /*
         * MOMO-002: 主流程
         * - 001 / 构造 ZeroLauncher 对象（启动器）
         *   - 001-1 / 通过 SPI 查找 BootIo 实现类
         *   - 001-2 / 通过实现类构造 HEnergy 对象
         *   - 001-3 / （配置器）将 HEnergy 对象作为参数构造 ZeroConfigurer 对象
         *   - 001-4 / 从 BootIo 中提取 HLauncher 对象
         */
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(clazz, args);
        container.start(Electy.whenContainer(VertxApplication::runInternal));
    }

    public static void runInternal(final Vertx vertx, final HConfig config) {

        /* Agent 类型处理新流程 */
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.AGENT), "component-agent");

        /* Worker 类型处理新流程 */
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.WORKER), "component-worker");

        /* Infusion 插件处理新流程  **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.INFUSION), "component-infix");

        /* Rule 验证规则处理流程 **/
        KRunner.run(() -> StubLinear.standalone(vertx, VertxComponent.CODEX), "component-codex");
    }
}
