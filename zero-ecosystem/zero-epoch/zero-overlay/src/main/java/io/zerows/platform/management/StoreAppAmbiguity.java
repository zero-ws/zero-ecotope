package io.zerows.platform.management;

import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.core.Vertx;
import io.zerows.specification.app.HApp;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * 此处应用管理的部分应该转换成静态的变量 APPS，主要原因如下：
 * <pre>
 *     1. {@link StoreApp} 属于高层跨应用管理，多维哈希表的结构可直接在整个全局环境中统一定位存储的应用信息
 *     2. 应用本身的存储若是实例型，没有任何意义，因为每次获取的都是不同实例，无法形成统一的应用存储结构
 * </pre>
 * 由于应用是在配置阶段初始化的，所以它和 {@link Vertx} 的实例没有任何交集，因此只可以通过
 * <pre>
 *     name = {@link HApp}
 *     id   = {@link HApp}
 *     ns   = {@link HApp}
 *     code 维度属于系统内部维度，此处不作为提取 App 的专用维度
 * </pre>
 *
 * @author lang : 2025-10-09
 */
@Slf4j
class StoreAppAmbiguity extends AbstractAmbiguity implements StoreApp {
    private static final MultiKeyMap<HApp> APPS = new MultiKeyMap<>();

    protected StoreAppAmbiguity(final HBundle owner) {
        super(owner);
    }

    @Override
    public Set<String> keys() {
        return APPS.keySet();
    }

    @Override
    public HApp valueGet(final String key) {
        if (Objects.isNull(key)) {
            return this.value();
        }
        return APPS.getOr(key);
    }

    @Override
    public HApp value() {
        if (APPS.keySet().isEmpty()) {
            return null;
        }
        return APPS.values().iterator().next();
    }

    @Override
    public StoreApp add(final HApp app) {
        if (Objects.nonNull(app) && Objects.nonNull(app.name())) {
            log.info("[ ZERO ] 应用 {} 已成功添加！", app.name());
            APPS.put(app.name(), app, app.id(), app.ns());
        }
        return this;
    }

    @Override
    public StoreApp remove(final HApp app) {
        if (Objects.nonNull(app) && Objects.nonNull(app.name())) {
            log.info("[ ZERO ] 应用 {} 已成功移除！", app.name());
            APPS.remove(app.name());
        }
        return this;
    }
}
