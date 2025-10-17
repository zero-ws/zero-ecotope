package io.zerows.extension.runtime.ambient.store;

import io.r2mo.typed.common.MultiKeyMap;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
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
            // id, name
            STORED.put(app.id(), ark, app.name());
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
            // id, name
            STORED.remove(app.id());
        }
        return this;
    }
}
