package io.zerows.cortex;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.specification.configuration.HAxis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-05-04
 */
public interface AxisSub extends HAxis<RunRoute> {
    Cc<String, AxisSub> CCT_SKELETON = Cc.openThread();

    static <T extends AxisSub> AxisSub ofOr(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return CCT_SKELETON.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    @Override
    default void mount(final RunRoute route) {
        this.mount(route, null);
    }

    void mount(RunRoute route, HBundle bundle);
}
