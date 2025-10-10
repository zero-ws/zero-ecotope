package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2025-10-10
 */
public interface Linear {

    Cc<String, Linear> CC_SKELETON = Cc.open();

    static Linear of(final VertxComponent type) {
        return of(null, type);
    }

    static Linear of(final HBundle bundle, final VertxComponent type) {
        return CC_SKELETON.pick(() -> LinearCenter.of(type, bundle), type.name());
    }

    default void start(final Class<?> clazz, final RunVertx runVertx) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    default void stop(final Class<?> clazz, final RunVertx runVertx) {
        throw new _60050Exception501NotSupport(this.getClass());
    }
}
