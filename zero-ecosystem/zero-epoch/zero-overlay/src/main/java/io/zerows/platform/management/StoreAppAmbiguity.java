package io.zerows.platform.management;

import io.r2mo.typed.common.MultiKeyMap;
import io.zerows.specification.app.HApp;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-09
 */
@Slf4j
class StoreAppAmbiguity extends AbstractAmbiguity implements StoreApp {
    private final MultiKeyMap<HApp> apps = new MultiKeyMap<>();

    protected StoreAppAmbiguity(final HBundle owner) {
        super(owner);
    }

    @Override
    public Set<String> keys() {
        return this.apps.keySet();
    }

    @Override
    public HApp valueGet(final String key) {
        return this.apps.getOr(key);
    }

    @Override
    public StoreApp add(final HApp app) {
        if (Objects.nonNull(app) && Objects.nonNull(app.name())) {
            log.info("[ ZERO ] 应用 {} 已成功添加！", app.name());
            this.apps.put(app.name(), app, app.id(), app.ns());
        }
        return this;
    }

    @Override
    public StoreApp remove(final HApp app) {
        if (Objects.nonNull(app) && Objects.nonNull(app.name())) {
            log.info("[ ZERO ] 应用 {} 已成功移除！", app.name());
            this.apps.remove(app.name());
        }
        return this;
    }
}
