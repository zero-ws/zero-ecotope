package io.zerows.cosmic;

import io.netty.util.concurrent.FastThreadLocal;
import io.r2mo.typed.exception.web._404NotFoundException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.zerows.cortex.management.StoreServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.jigsaw.NodeNetwork;
import io.zerows.epoch.jigsaw.NodeVertx;
import io.zerows.epoch.spec.options.SockOptions;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 【Standard Mode + L1/L2 Cache Architecture】
 * <p>
 * 架构说明：
 * 1. <b>L1 Cache (FastThreadLocal)</b>: 纳秒级访问，无锁无 Hash。用于运行时的高频访问（如果有后续业务需要取 Server）。
 * 2. <b>L2 Cache (StoreServer)</b>: 基于 ConcurrentHashMap 的管理层。用于全局监控、Shutdown 清理、以及 WatchDog 跨堆栈周期的状态恢复。
 * </p>
 */
@Slf4j
class StubServerService extends AbstractAmbiguity implements StubServer {

    // 🚀 L1 Cache: Netty 原生极速存储 (每个线程一个槽位)
    // 假设每个 Agent 线程只负责启动一个主 HTTP Server，这是 Vert.x 的标准模式。
    private static final FastThreadLocal<RunServer> FAST_CACHE = new FastThreadLocal<>();

    StubServerService(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Future<RunServer> createAsync(final RunVertx runVertx) {
        // 0) 基础校验
        if (Objects.isNull(runVertx) || Objects.isNull(runVertx.instance())) {
            return Future.failedFuture(new _404NotFoundException("[ ZERO ] Vertx 实例未找到，无法创建 HttpServer"));
        }
        final Vertx vertx = runVertx.instance();

        // 1) 解析配置
        final NodeVertx nodeVertx = runVertx.config();
        final NodeNetwork network = nodeVertx.networkRef();
        final HttpServerOptions serverOptions = network.server();

        String host = serverOptions.getHost();
        if (host == null || host.isBlank()) {
            host = "0.0.0.0";
        }
        final String serverName = host + ":" + serverOptions.getPort();

        // ==========================================================
        // ⚡️ Step 1: L1 Cache (极速路径)
        // 直接从线程局部变量数组中获取，速度最快 (~5ns)
        // ==========================================================
        final RunServer l1Server = FAST_CACHE.get();
        if (l1Server != null) {
            // 防御性检查：确保缓存的 Server 确实是我们要的那个 (防止多端口场景下的覆盖)
            if (l1Server.name().equals(serverName)) {
                log.debug("[ ZERO ] ( L1-Fast ) ⚡️ 命中 FastThreadLocal 缓存 -> {}", serverName);
                return Future.succeededFuture(l1Server);
            }
        }

        // ==========================================================
        // 🐢 Step 2: L2 Cache (管理路径 & 兜底复活)
        // 场景：WatchDog 重试时，L1 可能因为线程上下文清理丢失（视实现而定），
        // 或者我们需要确保全局管理 Map 中存在记录。
        // ==========================================================
        final RunServer l2Server = StoreServer.of().valueGet(serverName);
        if (l2Server != null) {
            log.debug("[ ZERO ] ( L2-Store ) 🐢 命中 StoreServer 缓存 (恢复 L1) -> {}", serverName);
            // 🔄 数据一致性同步：L2 有，L1 没有 -> 填充 L1
            FAST_CACHE.set(l2Server);
            return Future.succeededFuture(l2Server);
        }

        // ==========================================================
        // 🛠️ Step 3: 创建新实例 (无锁)
        // 只有 L1 和 L2 都没有时，才真正创建对象
        // ==========================================================
        try {
            log.debug("[ ZERO ] ( Standard ) ✨ 创建新 HttpServer 实例 -> {} [Thread: {}]",
                serverName, Thread.currentThread().getName());

            final HttpServer server = vertx.createHttpServer(serverOptions);
            final SockOptions sockOptions = network.sock();

            final RunServer newServer = new RunServer(serverName)
                .config(sockOptions)
                .config(serverOptions)
                .refRunVertx(runVertx)
                .instance(server)
                .build();

            // ==========================================================
            // 💾 Step 4: 双写 (Double-Write)
            // 保证一致性：既能极速访问，又能全局管理
            // ==========================================================

            // 1. 写入 L1 (Thread Local)
            FAST_CACHE.set(newServer);

            // 2. 写入 L2 (Global Map Management)
            // StoreServer 内部已实现线程 Key 隔离，安全写入
            StoreServer.of().add(newServer);

            log.debug("[ ZERO ] ( Sync ) 实例已同步至 L1 & L2 缓存 -> {}", serverName);

            return Future.succeededFuture(newServer);

        } catch (final Throwable e) {
            log.error("[ ZERO ] HttpServer 创建失败 -> name={}, 异常={}", serverName, e.toString());
            // 发生异常时清理 L1，防止脏数据
            FAST_CACHE.remove();
            return Future.failedFuture(e);
        }
    }
}