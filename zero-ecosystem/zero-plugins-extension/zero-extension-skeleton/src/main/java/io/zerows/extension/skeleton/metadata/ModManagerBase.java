package io.zerows.extension.skeleton.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.management.OCacheConfiguration;

import java.util.Objects;

/**
 * @author lang : 2025-12-22
 */
public abstract class ModManagerBase<CONFIG> implements ModManager<CONFIG> {
    private static final OCacheConfiguration STORE = OCacheConfiguration.of();
    private final MDConfiguration configuration;

    protected ModManagerBase(final String mid) {
        this.configuration = STORE.valueGet(mid);
    }

    @Override
    public JsonObject configuration() {
        if (Objects.isNull(this.configuration)) {
            return null;
        }
        return this.configuration.inConfiguration();
    }
}
