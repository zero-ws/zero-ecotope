package io.zerows.epoch.corpus.model.running;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.zerows.epoch.corpus.configuration.NodeVertx;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.modeling.running.RunInstance;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 核心数据结构
 * <pre><code>
 *     Cluster
 *      - Vertx-01 ...                              {@link RunVertx}  x 1
 *        - deploymentId-01-01 = Class<?>           {@link io.vertx.core.Verticle} x N
 *        - deploymentId-01-02 =
 *        - serverName =                            {@link RunServer} x N
 *          - appName  =                            {@link RunApp}    x N
 *        - router     =                            DoRouter          x 1
 *          - path     =                                              x N
 *            - route  =                            {@link Route}     x ( HTTP Method counter )
 *              event  =                            {@link Event}
 *      - Vertx-02
 * </code></pre>
 *
 * @author lang : 2024-05-03
 */
public class RunVertx implements RunInstance<Vertx> {

    private final String name;
    private final ConcurrentMap<String, Class<?>> deploymentMap = new ConcurrentHashMap<>();
    private Vertx vertxRef;
    private NodeVertx vertxConfig;

    public RunVertx(final String name) {
        this.name = name;
    }

    public HSetting setting() {
        return this.vertxConfig.setting();
    }

    public RunVertx config(final NodeVertx vertxConfig) {
        this.vertxConfig = vertxConfig;
        return this;
    }

    public void deploymentAdd(final String id, final Class<?> clazz) {
        if (Objects.nonNull(clazz)) {
            this.deploymentMap.put(id, clazz);
        }
    }

    public Set<String> deploymentFind(final Class<?> clazz) {
        final Set<String> stored = this.deploymentMap.keySet();
        if (Objects.isNull(this.vertxRef)) {
            return stored;
        }
        final Set<String> ids = this.vertxRef.deploymentIDs();
        return stored.stream().filter(ids::contains).filter(id -> {
            if (Objects.isNull(clazz)) {
                return true;
            }

            final Class<?> storedCls = this.deploymentMap.get(id);
            return storedCls == clazz;
        }).collect(Collectors.toSet());
    }

    public void deploymentRemove(final String id) {
        this.deploymentMap.remove(id);
    }

    // ---------------- 接口专用方法
    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Vertx instance() {
        return this.vertxRef;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RunVertx instance(final Vertx vertx) {
        this.vertxRef = vertx;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NodeVertx config() {
        return this.vertxConfig;
    }

    @Override
    public boolean isOk() {
        return Ut.isNotNil(this.name) && Objects.nonNull(this.vertxRef);
    }

    @Override
    public boolean isOk(final int hashCode) {
        if (Objects.isNull(this.vertxRef)) {
            return false;
        }
        return this.vertxRef.hashCode() == hashCode;
    }
}
