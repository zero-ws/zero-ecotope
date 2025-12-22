package io.zerows.cosmic;

import io.r2mo.typed.exception.web._404NotFoundException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.shareddata.Lock;
import io.zerows.cortex.management.StoreServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 只创建一次 RunServer，并发复用；全链路异步（无内置看门狗）。
 * 日志：中文 + [ ZERO ] 前缀；成功路径 debug、异常 warn/error。
 */
@Slf4j
class StubServerService extends AbstractAmbiguity implements StubServer {

    // 可配置：锁超时
    private static final long LOCK_TIMEOUT_MS = 10_000L;

    StubServerService(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Future<RunServer> createAsync(final RunVertx runVertx) {
        final Promise<RunServer> promise = Promise.promise();

        // 0) 校验
        if (Objects.isNull(runVertx) || Objects.isNull(runVertx.instance())) {
            throw new _404NotFoundException("[ ZERO ] Vertx 实例未找到，无法创建 HttpServer");
        }
        final Vertx vertx = runVertx.instance();

        // 1) 解析配置 & serverName
        final NodeVertx nodeVertx = runVertx.config();
        final NodeNetwork network = nodeVertx.networkRef();
        final HttpServerOptions serverOptions = network.server();

        String host = serverOptions.getHost();
        if (host == null || host.isBlank()) {
            host = "0.0.0.0";
        }
        final String serverName = host + ":" + serverOptions.getPort();
        final boolean clustered = vertx.isClustered();

        log.debug("[ ZERO ] 创建 RunServer 开始 -> name={}, 集群={}, vertxHash={}",
            serverName, clustered, System.identityHashCode(vertx));
        log.debug("[ ZERO ] HttpServerOptions -> host={}, port={}, ssl={}, alpn={}",
            host, serverOptions.getPort(), serverOptions.isSsl(), serverOptions.isUseAlpn());

        // 2) 锁外快速命中
        final RunServer existed = StoreServer.of().valueGet(serverName);
        if (existed != null) {
            log.debug("[ ZERO ] 命中已存在 RunServer -> {}", serverName);
            return Future.succeededFuture(existed);
        }

        // 3) 以运行态选择锁类型
        final String keyLock = Thread.currentThread().getName() + "@server:create:" + serverName;
        final Future<Lock> futLock = clustered
            ? vertx.sharedData().getLockWithTimeout(keyLock, LOCK_TIMEOUT_MS)
            : vertx.sharedData().getLocalLockWithTimeout(keyLock, LOCK_TIMEOUT_MS);

        log.debug("[ ZERO ] 准备获取锁 -> key={}, timeoutMs={}, 集群={}", keyLock, LOCK_TIMEOUT_MS, clustered);

        futLock.onComplete(ar -> {
            if (!ar.succeeded()) {
                log.warn("[ ZERO ] 获取锁失败 -> key={}, 原因={}", keyLock, ar.cause().toString());
                log.debug("[ ZERO ] 获取锁失败堆栈：", ar.cause());
                promise.fail(ar.cause());
                return;
            }

            final Lock lock = ar.result();
            log.debug("[ ZERO ] 已获取锁 -> key={}", keyLock);

            final long c0 = System.nanoTime();
            try {
                // 4) 【临界区极短】双检 -> 构造 -> 登记
                RunServer runServer = StoreServer.of().valueGet(serverName);
                if (runServer == null) {
                    log.debug("[ ZERO ] 创建 HttpServer -> {}", serverName);
                    final HttpServer server = vertx.createHttpServer(serverOptions);
                    final SockOptions sockOptions = network.sock();

                    runServer = new RunServer(serverName)
                        .config(sockOptions)
                        .config(serverOptions)
                        .refRunVertx(runVertx)
                        .instance(server)
                        .build();

                    StoreServer.of().add(runServer);
                    log.debug("[ ZERO ] RunServer 创建并注册完成 -> {}", serverName);
                } else {
                    log.debug("[ ZERO ] 二次检查命中：复用 RunServer -> {}", serverName);
                }

                final long totalMs = (System.nanoTime() - c0) / 1_000_000;
                log.debug("[ ZERO ] 创建流程完成（锁内）-> name={}, criticalMs={}ms", serverName, totalMs);
                promise.complete(runServer);

            } catch (final Throwable e) {
                log.error("[ ZERO ] 临界区异常 -> name={}, 异常={}", serverName, e.toString());
                log.debug("[ ZERO ] 临界区异常堆栈：", e);
                promise.fail(e);

            } finally {
                try {
                    lock.release();
                    log.debug("[ ZERO ] 已释放锁 -> key={}", keyLock);
                } catch (final Throwable re) {
                    log.warn("[ ZERO ] 释放锁异常 -> key={}, 原因={}", keyLock, re.toString());
                    log.debug("[ ZERO ] 释放锁异常堆栈：", re);
                }
            }
        });

        return promise.future();
    }
}
