package io.zerows.plugins.security;

import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationContext;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ProfileAuthorizationImpl implements ProfileAuthorization {
    private final Set<String> permissions = new HashSet<>();

    ProfileAuthorizationImpl(final Set<String> permissions) {
        this.permissions.addAll(Objects.requireNonNull(permissions));
    }

    @Override
    public Set<String> permissions() {
        return this.permissions;
    }

    @Override
    public boolean match(final AuthorizationContext context) {
        Objects.requireNonNull(context);
        final User user = context.user();
        if (user != null) {
            final Authorization resolved = ProfileAuthorization.create(this.permissions);
            return user.authorizations().verify(resolved);
        }
        return false;
    }

    @Override
    public boolean verify(final Authorization otherAuthorization) {
        Objects.requireNonNull(otherAuthorization);
        if (otherAuthorization instanceof final ProfileAuthorization permission) {
            return this.permissions.containsAll(permission.permissions());
        }
        return false;
    }
}
