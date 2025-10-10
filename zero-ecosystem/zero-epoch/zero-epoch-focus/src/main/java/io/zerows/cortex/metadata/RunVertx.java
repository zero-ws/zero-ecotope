package io.zerows.cortex.metadata;

import io.vertx.core.Vertx;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-10
 */
public class RunVertx implements RunInstance<Vertx> {

    private final String name;
    private final ConcurrentMap<String, Class<?>> deployments = new ConcurrentHashMap<>();
    private Vertx vertxRef;
    private NodeVertx vertxStatic;

    public RunVertx(final String name) {
        this.name = name;
    }

    public void addDeployment(final String id, final Class<?> clazz) {
        if (Objects.nonNull(clazz)) {
            this.deployments.put(id, clazz);
        }
    }

    public Set<String> findDeployment(final Class<?> clazz) {
        final Set<String> stored = this.deployments.keySet();
        if (Objects.isNull(this.vertxRef)) {
            return stored;
        }
        final Set<String> ids = this.vertxRef.deploymentIDs();
        return stored.stream().filter(ids::contains).filter(id -> {
            if (Objects.isNull(clazz)) {
                return true;
            }
            final Class<?> storedCls = this.deployments.get(id);
            return storedCls == clazz;
        }).collect(Collectors.toSet());
    }

    public void removeDeployment(final String id) {
        this.deployments.remove(id);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isOk() {
        return Ut.isNotNil(this.name) && Objects.nonNull(this.vertxRef);
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
        return this.vertxStatic;
    }

    public RunVertx config(final NodeVertx vertxStatic) {
        this.vertxStatic = vertxStatic;
        return this;
    }

    @Override
    public boolean isOk(final int hashCode) {
        if (Objects.isNull(this.vertxRef)) {
            return false;
        }
        return this.vertxRef.hashCode() == hashCode;
    }
}
