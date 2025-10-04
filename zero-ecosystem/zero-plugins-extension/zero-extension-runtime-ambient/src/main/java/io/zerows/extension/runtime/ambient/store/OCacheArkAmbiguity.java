package io.zerows.extension.runtime.ambient.store;

import io.zerows.epoch.metadata.MultiKeyMap;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-07-08
 */
class OCacheArkAmbiguity extends AbstractAmbiguity implements OCacheArk {

    private static final MultiKeyMap<HArk> STORED = new MultiKeyMap<>();

    OCacheArkAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<String> keys() {
        return STORED.keySet();
    }

    @Override
    public HArk valueGet(final String key) {
        return STORED.getOr(key);
    }

    @Override
    public OCacheArk add(final HArk ark) {
        if (Objects.isNull(ark)) {
            return this;
        }
        final HApp app = ark.app();
        if (Objects.nonNull(app)) {
            // appId, name
            STORED.put(app.appId(), ark, app.name());
        }
        return this;
    }

    @Override
    public OCacheArk remove(final HArk ark) {
        if (Objects.isNull(ark)) {
            return this;
        }
        final HApp app = ark.app();
        if (Objects.nonNull(app)) {
            // appId, name
            STORED.remove(app.appId());
        }
        return this;
    }
}
