package io.zerows.cortex.management;

import io.r2mo.typed.common.MultiKeyMap;
import io.vertx.core.Vertx;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.jigsaw.NodeVertx;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-30
 */
@Slf4j
class StoreVertxAmbiguity extends AbstractAmbiguity implements StoreVertx {

    private static final MultiKeyMap<RunVertx> RUNNING = new MultiKeyMap<>();

    StoreVertxAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<String> keys() {
        return RUNNING.keySet();
    }

    @Override
    public Vertx vertx(final String name) {
        final RunVertx runVertx = RUNNING.getOr(name);
        return Objects.isNull(runVertx) ? null : runVertx.instance();
    }

    @Override
    public Vertx vertx() {
        if (1 != RUNNING.values().size()) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        final RunVertx runVertx = RUNNING.values().iterator().next();
        return Objects.isNull(runVertx) ? null : runVertx.instance();
    }

    @Override
    public RunVertx valueGet(final int hashCode) {
        final String key = String.valueOf(hashCode);
        final RunVertx runVertx = RUNNING.getOr(key);
        if (runVertx.isOk() && runVertx.isOk(hashCode)) {
            return runVertx;
        }
        return null;
    }

    @Override
    public StoreVertx add(final RunVertx runVertx) {
        Objects.requireNonNull(runVertx);
        if (runVertx.isOk()) {
            final Vertx vertxRef = runVertx.instance();
            final String id = String.valueOf(vertxRef.hashCode());
            log.info("[ ZERO ] 添加运行实例：name = {}, hashCode = {}", runVertx.name(), id);
            RUNNING.put(id, runVertx, runVertx.name());

            // 底层添加
            final NodeVertx nodeVertx = runVertx.config();
            NodeStore.add(id, nodeVertx);
        }
        return this;
    }

    @Override
    public RunVertx valueGet(final String hashCode) {
        final RunVertx runVertx = RUNNING.getOr(hashCode);
        if (runVertx.isOk()) {
            return runVertx;
        }
        return null;
    }

    @Override
    public StoreVertx remove(final String name) {
        if (Ut.isNotNil(name)) {
            RUNNING.remove(name);

            // 底层移除
            NodeStore.remove(name);
        }
        return this;
    }
}
