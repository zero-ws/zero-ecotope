package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.module.ambient.management.OCacheArk;
import io.zerows.extension.module.ambient.component.Cabinet;
import io.zerows.extension.module.ambient.component.CabinetApp;
import io.zerows.extension.module.ambient.component.CabinetSource;
import io.zerows.extension.module.ambient.component.UniteArk;
import io.zerows.extension.module.ambient.component.UniteArkSource;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.program.Ux;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.support.base.FnBase;

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
            // id = XApp
            () -> Cabinet.<XApp>of(CabinetApp::new).loadAsync(container),
            // id = XSource
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
