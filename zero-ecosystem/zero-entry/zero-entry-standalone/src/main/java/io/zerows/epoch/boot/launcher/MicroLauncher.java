package io.zerows.epoch.boot.launcher;

import io.r2mo.function.Fn;
import io.vertx.core.Vertx;
import io.zerows.epoch.corpus.container.exception._40037Exception500RpcEnvironment;
import io.zerows.epoch.corpus.container.store.BootStore;
import io.zerows.epoch.corpus.invocation.micro.uddi.UddiRegistry;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.access.HLauncher;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;

import java.util.function.Consumer;

/**
 * 微服务的 API Gateway 启动器
 *
 * @author lang : 2023-05-30
 */
public class MicroLauncher implements HLauncher<Vertx> {
    private static final BootStore STORE = BootStore.singleton();

    private transient final HLauncher<Vertx> zero;

    public MicroLauncher() {
        this.zero = Ut.singleton(ZeroLauncher.class);
    }

    /**
     * 微服务启动之前会多做一层配置
     * <pre><code>
     *     1. 检查 ETCD 是否打开（如果未打开则直接退出）
     *     2. 处理 {@link UddiRegistry}
     *        - 读取配置文件 vertx-micro.yml
     *        - 若开启了 ETCD 则连接配置中心
     *        - 初始化每一个节点实现完整的执行流程
     * </code></pre>
     */
    @Override
    public <T extends HConfig> void start(final HConfig.HOn<T> on, final Consumer<Vertx> server) {
        Fn.jvmKo(!STORE.isEtcd(), _40037Exception500RpcEnvironment.class);
        // 初始化微服务环境
        final UddiRegistry registry = Ut.singleton(UddiRegistry.class);
        final HBoot boot = STORE.boot();
        registry.initialize(boot.target());

        this.zero.start(on, server);
    }

    @Override
    public <T extends HConfig> void stop(final HConfig.HOff<T> off, final Consumer<Vertx> server) {

    }
}
