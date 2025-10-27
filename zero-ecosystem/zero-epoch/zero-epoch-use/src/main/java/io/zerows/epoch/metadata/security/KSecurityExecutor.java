package io.zerows.epoch.metadata.security;

import io.zerows.epoch.annotations.security.Authenticate;
import io.zerows.epoch.annotations.security.Authorized;
import io.zerows.epoch.annotations.security.AuthorizedResource;
import io.zerows.epoch.annotations.security.AuthorizedUser;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 安全专用方法集，用于存储安全相关的方法引用
 */
@Data
public class KSecurityExecutor implements Serializable {
    /**
     * 401: 认证专用方法
     * {@link Authenticate}
     */
    private Method authenticate;
    /**
     * 403: 授权专用方法
     * {@link Authorized}
     */
    private Method authorization;
    /**
     * 403: 资源标记专用
     * {@link AuthorizedResource}
     */
    private Method resource;
    /**
     * 403: 授权用户专用方法
     * {@link AuthorizedUser}
     */
    private Method user;

    @Override
    public String toString() {
        return "Against{" +
            "authenticate=" + this.authenticate +
            ", authorization=" + this.authorization +
            ", resource=" + this.resource +
            ", user=" + this.user +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KSecurityExecutor against = (KSecurityExecutor) o;
        return this.authenticate.equals(against.authenticate) &&
            Objects.equals(this.authorization, against.authorization) &&
            Objects.equals(this.resource, against.resource) &&
            Objects.equals(this.user, against.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.authenticate, this.authorization, this.resource, this.user);
    }
}
