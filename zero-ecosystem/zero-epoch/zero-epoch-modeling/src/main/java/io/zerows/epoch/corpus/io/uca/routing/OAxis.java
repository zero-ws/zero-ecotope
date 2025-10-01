package io.zerows.epoch.corpus.io.uca.routing;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.configuration.boot.HAxis;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * 新接口，用于替换原始的路由执行器等相关处理操作，动态管理发布过程中可执行的部分，针对不同发布执行相关处理，此处
 * {@link RunServer} 内置包含了
 *
 * @author lang : 2024-05-04
 */
public interface OAxis extends HAxis<RunServer> {

    Cc<String, OAxis> CCT_SKELETON = Cc.openThread();

    static <T extends OAxis> OAxis ofOr(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return CCT_SKELETON.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    @Override
    default void mount(final RunServer server) {
        this.mount(server, null);
    }

    void mount(RunServer server, Bundle bundle);
}
