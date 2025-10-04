package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.component.log.OLog;
import io.zerows.cosmic.handler.ZeroHttpAgent;
import io.zerows.cosmic.handler.ZeroHttpWorker;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Verticle 实例管理器
 * 键值计算和其他不同，此处构造 StubLinear 必须包含 Verticle 的类名，如
 * <pre><code>
 *     1. {@link ZeroHttpAgent}
 *     2. {@link ZeroHttpWorker}
 *     3. 扩展模式 ZeroHttpRegistry 或其他
 * </code></pre>
 *
 * @author lang : 2024-05-03
 */
public interface StubLinear {

    Cc<String, StubLinear> CC_SKELETON = Cc.open();

    ConcurrentMap<VertxComponent, Function<HBundle, StubLinear>> RUNNER = new ConcurrentHashMap<>() {
        {
            this.put(VertxComponent.AGENT, LinearAgent::new);
            this.put(VertxComponent.WORKER, LinearWorker::new);
            this.put(VertxComponent.IPC, LinearRpc::new);
            this.put(VertxComponent.CODEX, LinearCodex::new);
            this.put(VertxComponent.INFUSION, LinearInfusion::new);
        }
    };

    static StubLinear of(final HBundle bundle, final VertxComponent type) {
        return StubLinear.CC_SKELETON.pick(() -> {
            Ut.Log.vertx(StubLinear.class).info("StubLinear will be initialized by type = {}", type);
            final Function<HBundle, StubLinear> constructorFn = RUNNER.get(type);
            Objects.requireNonNull(constructorFn);
            return constructorFn.apply(bundle);
        }, type.name());
    }

    // --------------------- 启动执行 ---------------------
    static void standalone(final Vertx vertx, final VertxComponent type) {
        final RunVertx runVertx = StoreVertx.of(null).valueGet(vertx.hashCode());
        final Set<Class<?>> scanClass = OCacheClass.entireValue(type);
        final StubLinear linear = StubLinear.of(null, type);
        linear.initialize(scanClass, runVertx);
    }

    // --------------------- 行为专用 ---------------------

    /**
     * 此处 initialize 方法暂时不可以拿掉，主要原因在于 {@link Infusion} 中的 Infix 架构必须依赖一个 initialize 方法来做
     * 组件初始化，这个组件初始化不可以出错，所以这种情况下，必须让 Infusion 可用
     *
     * @param classSet 可用注解类
     * @param runVertx 运行实例
     */
    default void initialize(final Set<Class<?>> classSet, final RunVertx runVertx) {
        classSet.forEach(clazz -> this.runDeploy(clazz, runVertx));
    }


    default void runDeploy(final Class<?> clazz, final RunVertx runVertx) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    default void runUndeploy(final Class<?> clazz, final RunVertx runVertx) {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    default OLog logger() {
        return Ut.Log.vertx(this.getClass());
    }
}
