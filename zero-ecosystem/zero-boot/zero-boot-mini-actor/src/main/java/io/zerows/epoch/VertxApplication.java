package io.zerows.epoch;

import io.r2mo.vertx.dbe.FactoryDBAsync;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.HttpServerOptions;
import io.zerows.cortex.AxisDynamicFactory;
import io.zerows.cortex.AxisSockFactory;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.cosmic.bootstrap.Linear;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.epoch.basicore.option.RpcOptions;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.metadata.KRunner;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.configuration.HSetting;
import io.zerows.spi.BootIo;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 标准启动器，直接启动 Vertx 实例处理 Zero 相关的业务逻辑
 */
@Slf4j
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
     *     - {@see ZeroLauncher#configure}
     *       / 通过 {@link BootIo} 中提取的 {@link HEnergy} 来构造的内置对象
     * </pre>
     * 此处的 configure 是一个调和类，用于协调 HEnergy 与 HLauncher 之间的关系，HEnergy 本身并非直接参与启动
     * 流程，而是通过 configure 来间接影响启动过程。启动模式如下:
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
     *     - {@link EnvironmentVariable#R2MO_NACOS_ADDR} -> nacos 端地址
     *     - {@link EnvironmentVariable#R2MO_NACOS_USERNAME} -> nacos 登录用户名
     *     - {@link EnvironmentVariable#R2MO_NACOS_PASSWORD} -> nacos 登录密码
     * </pre>
     * 对象数量汇总
     * <pre>
     *     vertx.yml / vertx-boot.yml 参考 {@link YmSpec}
     *     1. {@link HBoot}                         x 1                 核心启动配置
     *     2. {@link HSetting}                      x 1                 配置对象（静态）
     *        {@link HEnergy}                       x 1                 配置对象（动态）
     *     3. {@link NodeNetwork}                   x 1                 集群、网络、环境（静态）
     *            {@link HttpServerOptions}         x 1
     *            {@link ClusterOptions}            x 1
     *            {@link SockOptions}               x 1
     *            {@link RpcOptions}                x 1                 （保留）
     *        {@link NodeVertx}                     x N                 Vert.x 实例（静态）
     *            {@link DeploymentOptions}         x N
     *            {@link VertxOptions}              x 1
     *            {@link DeliveryOptions}           x 1
     *            {@link EventBusOptions}           x 1                 = {@link VertxOptions#getEventBusOptions()}
     *     4. {@link RunServer}                     x 1                 服务器信息
     *        {@link RunVertx}                      x N                 Vert.x 实例（动态）
     *        {@link RunRoute}                      x N                 路由管理器 -> server handler
     *     5. {@link HApp}                          x N                 应用对象（内层）
     *        {@link HArk}                          x N                 应用对象（外层）
     *        *: 正常应用启动器只有一个 {@link HApp} 对象
     * </pre>
     *
     * @param clazz 启动主类
     * @param args  启动参数
     */
    public static void run(final Class<?> clazz, final String... args) {
        /*
         * MOMO-001: SPI 监控注册
         */
        HPI.registry(
            FactoryDBAsync.class,       // 异步DBE
            AxisSockFactory.class,      // WebSocket
            AxisDynamicFactory.class    // 动态路由
        );


        /*
         * MOMO-002: 主流程
         * - 001 / 构造 ZeroLauncher 对象（启动器）
         *   - 001-1 / 通过 SPI 查找 BootIo 实现类
         *   - 001-2 / 通过实现类构造 HEnergy 对象
         *   - 001-3 / （配置器）将 HEnergy 对象作为参数构造 ZeroConfigurer 对象
         *   - 001-4 / 从 BootIo 中提取 HLauncher 对象
         */
        final ZeroLauncher<Vertx> container = ZeroLauncher.create(clazz, args);
        container.start((vertx, config) -> {
            /*
             * MOMO-003: 启动核心处理流程
             * - AGENT 启动
             * - WORKER 启动
             */
            final RunVertx runVertx = StoreVertx.of().valueGet(vertx.hashCode());

            runInternal(runVertx, VertxComponent.AGENT);

            runInternal(runVertx, VertxComponent.WORKER);
        });
    }

    private static void runInternal(final RunVertx runVertx, final VertxComponent type) {
        final Set<Class<?>> scanClass = OCacheClass.entireValue(type);
        final Linear linear = Linear.of(type);
        scanClass.forEach(scanned -> KRunner.run(
            () -> linear.start(scanned, runVertx),                                    // 发布逻辑
            "momo-" + type.name().toLowerCase() + "-" + scanned.getSimpleName())      // 线程名称
        );
    }
}
