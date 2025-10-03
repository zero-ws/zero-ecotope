package io.zerows.epoch.corpus.container.uca.store;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.log.OLog;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * Vertx实例管理器，有可能跨越多个集群而存在，所以此处用来执行 Vertx 实例的整体管理流程，可同时支持两种环境
 * <pre><code>
 *     1. 单点环境 / 集群环境
 *     2. OSGI 环境
 * </code></pre>
 *
 * 正常组件在使用过程中都指定 Bundle 来构建，所以从这点意义上讲，BundleInternalConfig 只用于配置管理，而且用于
 * 前置提供服务消费的对应传递，如
 * <pre><code>
 *     Bundle A  ---> 调用 Bundle B 的服务，而又要将 Bundle A 本身传过去时会使用
 * </code></pre>
 *
 * 在实际环境中，不同组件前缀（都可以发布成 Service 服务）
 * <pre><code>
 *     1. Energy-：（对外开放的服务）负责配置管理、静态模式，运行之后直接放到内存或缓存中，多为数据结构。
 *     3. Each-：（对内服务，对外不开放）表示组件管理、动态模式，多为内部使用，不对外提供服务。
 * </code></pre>
 *
 * @author lang : 2024-04-30
 */
public interface StubVertx {

    Cc<String, StubVertx> CC_SKELETON = Cc.open();

    static StubVertx of(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, StubVertxService.class);
        return CC_SKELETON.pick(() -> new StubVertxService(bundle), cacheKey);
    }

    static StubVertx of() {
        return of(null);
    }

    // --------------------- 行为专用 ---------------------

    /**
     * 创建 Vertx 实例，异步返回用于统一 Cluster 模式和非 Cluster 模式下的 Vertx 对应实例信息
     *
     * @param nodeVertx {@link NodeVertx}
     *
     * @return {@link Vertx}
     */
    Future<RunVertx> createAsync(NodeVertx nodeVertx, boolean clustered);

    Vertx get(String name);

    StubVertx add(String name, RunVertx vertx);

    StubVertx remove(String name);

    default OLog logger() {
        return Ut.Log.vertx(this.getClass());
    }
}
