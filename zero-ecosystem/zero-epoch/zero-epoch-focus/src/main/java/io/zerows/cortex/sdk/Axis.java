package io.zerows.cortex.sdk;

import io.r2mo.typed.cc.Cc;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.specification.configuration.HAxis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 新接口，用于替换原始的路由执行器等相关处理操作，动态管理发布过程中可执行的部分，针对不同发布执行相关处理，此处
 * {@link RunServer} 内置包含了
 *
 * @author lang : 2024-05-04
 */
public interface Axis extends HAxis<RunServer> {

    Cc<String, Axis> CCT_SKELETON = Cc.openThread();

    static <T extends Axis> Axis ofOr(final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return CCT_SKELETON.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    @Override
    default void mount(final RunServer server) {
        this.mount(server, null);
    }

    void mount(RunServer server, HBundle bundle);
}
