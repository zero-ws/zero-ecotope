package io.zerows.epoch.metadata.security;

import io.zerows.platform.enums.SecurityType;
import io.zerows.specification.atomic.HCopier;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Secure class container for special class extraction.
 * Scanned ( KMetadata ) for each @Wall.
 */
@Slf4j
public class SecurityMeta implements Serializable, Comparable<SecurityMeta>, HCopier<SecurityMeta> {
    /**
     * Current config
     */
    private final ConcurrentMap<String, SecurityConfig> items = new ConcurrentHashMap<>();

    /**
     * The wall path to be security limitation
     */
    private String path;
    /**
     * Current wall order
     */
    private int order;
    /**
     * Current wall type
     */
    private SecurityType type;
    /**
     * Proxy instance
     */
    private Object proxy;

    private Class<?> handler;

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public SecurityType getType() {
        return this.type;
    }

    public SecurityMeta setType(final SecurityType type) {
        this.type = type;
        return this;
    }

    public Object getProxy() {
        return this.proxy;
    }

    public void setProxy(final Object proxy) {
        this.proxy = proxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends SecurityMeta> CHILD copy() {
        final SecurityMeta aegis = new SecurityMeta();
        // Final
        //        aegis.authorizer.setResource(this.authorizer.getResource());
        //        aegis.authorizer.setAuthorization(this.authorizer.getAuthorization());
        //        aegis.authorizer.setAuthenticate(this.authorizer.getAuthenticate());
        //        aegis.authorizer.setUser(this.authorizer.getUser());
        // Reference
        aegis.handler = this.handler;
        aegis.items.putAll(this.items);
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
        return Ut.compareTo(this, target, (left, right) -> {
            // 1. Compare Path
            int result = Ut.compareTo(left.getPath(), right.getPath());
            if (0 == result) {
                // 2. Compare Order
                result = Ut.compareTo(left.getOrder(), right.getOrder());
            }
            return result;
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.order, this.type, this.proxy);
    }

}
