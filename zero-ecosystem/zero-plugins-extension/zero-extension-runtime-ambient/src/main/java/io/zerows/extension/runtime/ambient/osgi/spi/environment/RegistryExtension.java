package io.zerows.extension.runtime.ambient.osgi.spi.environment;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.corpus.Ux;
import io.zerows.exception.web._60050Exception501NotSupport;
import io.zerows.support.FnBase;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.runtime.ambient.store.OCacheArk;
import io.zerows.extension.runtime.ambient.uca.boot.*;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.boot.HRegistry;

import java.util.List;
import java.util.Set;

/**
 * 扩展容器注册器
 * <pre><code>
 *     1. 此注册器仅适用于 zero-ambient 扩展模块的引入（非云端）
 *     2. 此注册器内置会访问 X_APP / X_SOURCE 构造 {@link HArk} 容器数据
 *     3. 由于容器本身使用了 {@link Vertx}，所以只支持异步模式的注册
 * </code></pre>
 * 注册器中不可以访问 {@link HAmbient} 接口，因为此时该接口还未执行初始化
 *
 * @author lang : 2023-06-06
 */
public class RegistryExtension implements HRegistry<Vertx> {
    @Override
    public Set<HArk> registry(final Vertx container, final HConfig config) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    @Override
    public Future<Set<HArk>> registryAsync(final Vertx container, final HConfig config) {
        return FnBase.combineT(
            // appId = XApp
            () -> Cabinet.<XApp>of(CabinetApp::new).loadAsync(container),
            // appId = XSource
            () -> Cabinet.<List<XSource>>of(CabinetSource::new).loadAsync(container),
            // 1 XApp + N XSource
            (app, sources) -> Ux.future(
                UniteArk.<List<XSource>>of(UniteArkSource::new).compile(app, sources)
            )
        ).compose(arkSet -> {
            // 调用内置存储，新版 OSGI 环境专用
            final OCacheArk cacheArk = OCacheArk.of();
            cacheArk.add(arkSet);
            return Ux.future(arkSet);
        });
    }
}
