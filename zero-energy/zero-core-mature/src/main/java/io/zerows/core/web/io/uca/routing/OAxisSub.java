package io.zerows.core.web.io.uca.routing;

import io.r2mo.typed.cc.Cc;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.running.RunRoute;
import io.zerows.specification.configuration.boot.HAxis;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-04
 */
public interface OAxisSub extends HAxis<RunRoute> {
    Cc<String, OAxisSub> CCT_SKELETON = Cc.openThread();

    static <T extends OAxisSub> OAxisSub ofOr(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return CCT_SKELETON.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    @Override
    default void mount(final RunRoute route) {
        this.mount(route, null);
    }

    void mount(RunRoute route, Bundle bundle);
}
