package io.zerows.cortex.management;

import io.vertx.core.http.HttpServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 【线程隔离存储模型】
 * <p>
 * 核心目标：支持 Vert.x Standard Scaling (多实例并发)。
 * 实现原理：虽然使用 static 全局 Map，但 Key 绑定了 {@code Thread.currentThread().getName()}。
 * 效果：
 * 1. 不同的 EventLoop 线程访问同一个 serverName (如 0.0.0.0:7185) 时，会生成不同的 Key。
 * 2. 实现了逻辑上的“每个线程独享一个 RunServer 实例”。
 * 3. 保留了全局管理能力（可以通过遍历 Map 管理所有线程的实例）。
 * </p>
 *
 * @author lang : 2024-05-03
 */
@Slf4j
class StoreServerAmbiguity extends AbstractAmbiguity implements StoreServer {

    // 全局静态存储，但 Key 包含线程信息，互不冲突
    private static final ConcurrentMap<String, RunServer> RUNNING = new ConcurrentHashMap<>();

    StoreServerAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    /**
     * 核心魔法：生成线程隔离的 Key
     * 格式：{ThreadName}::{ServerName}
     * 例如：vert.x-eventloop-thread-1::0.0.0.0:7185
     */
    private String serverKey(final String serverName) {
        if (Ut.isNil(serverName)) {
            return null;
        }
        // 使用 :: 分隔，视觉更清晰，且 URL 中不常用 ::
        return Thread.currentThread().getName() + "::" + serverName;
    }

    @Override
    public HttpServer server(final String name) {
        final RunServer runServer = this.valueGet(name);
        return Objects.isNull(runServer) ? null : runServer.instance();
    }

    @Override
    public RunServer valueGet(final String name) {
        final String uniqueKey = this.serverKey(name);
        if (uniqueKey == null) {
            return null;
        }
        return RUNNING.get(uniqueKey);
    }

    @Override
    public StoreServer add(final RunServer runServer) {
        Objects.requireNonNull(runServer);
        if (runServer.isOk()) {
            final String uniqueKey = this.serverKey(runServer.name());
            if (uniqueKey != null) {
                // 仅存入当前线程的插槽，绝对不会覆盖其他线程的实例
                RUNNING.put(uniqueKey, runServer);
                log.debug("[ ZERO ] ( Store ) 实例已注册：key={}, hash={}", uniqueKey, runServer.hashCode());
            }
        }
        return this;
    }

    @Override
    public StoreServer remove(final RunServer runServer) {
        if (Objects.nonNull(runServer)) {
            // 直接根据实例名称删除当前线程对应的那个
            return this.remove(runServer.name());
        }
        return this;
    }

    @Override
    public StoreServer remove(final String name) {
        final String uniqueKey = this.serverKey(name);
        if (uniqueKey != null) {
            // ⚠️ 修正逻辑：只移除【当前线程】持有的那个实例
            // 原逻辑会把所有线程的同名 Server 全删掉，这是很危险的
            final RunServer removed = RUNNING.remove(uniqueKey);
            if (removed != null) {
                log.debug("[ ZERO ] ( Store ) 实例已移除：key={}", uniqueKey);
            }
        }
        return this;
    }

    /**
     * 获取所有 Key（包含线程前缀）。
     * 用于全局监控或 Shutdown 时的清理。
     */
    @Override
    public Set<String> keys() {
        return RUNNING.keySet();
    }
}