package io.zerows.epoch.metadata.security;

import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Wall;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.specification.atomic.HCopier;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * 安全墙 {@link Wall} 的专用配置
 */
@Slf4j
@Data
public class SecurityMeta implements Serializable, Comparable<SecurityMeta>, HCopier<SecurityMeta> {
    private String path;
    private int order;
    private SecurityType type;
    private WallExecutor proxy;

    private Class<?> handler;

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends SecurityMeta> CHILD copy() {
        final SecurityMeta aegis = new SecurityMeta();
        aegis.handler = this.handler;
        aegis.proxy = this.proxy;
        aegis.order = this.order;
        aegis.path = this.path;
        aegis.type = this.type;
        return (CHILD) aegis;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SecurityMeta wall)) {
            return false;
        }
        return this.order == wall.order &&
            Objects.equals(this.path, wall.path) &&
            this.type == wall.type &&
            Objects.equals(this.proxy, wall.proxy);
    }

    @Override
    public int compareTo(final @NotNull SecurityMeta target) {
        // 1. Compare Path
        int result = Objects.compare(this.getPath(), target.getPath(), String::compareTo);
        if (0 == result) {
            // 2. Compare Order
            result = Integer.compare(this.getOrder(), target.getOrder());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.order, this.type, this.proxy);
    }

    public String id(final Vertx vertx) {
        return vertx.hashCode() + "@" + this.hashCode();
    }

    public String idPre(final Vertx vertx) {
        return vertx.hashCode() + "@" + this.path + this.type;
    }
}
