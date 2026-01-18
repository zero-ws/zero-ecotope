package io.zerows.plugins.security;

import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationContext;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class ProfileAuthorizationImpl implements ProfileAuthorization {
    private final ConcurrentMap<String, Set<String>> permissionMap = new ConcurrentHashMap<>();

    ProfileAuthorizationImpl(final ConcurrentMap<String, Set<String>> permissionMap) {
        Objects.requireNonNull(permissionMap);
        this.permissionMap.putAll(permissionMap);
    }

    @Override
    public ConcurrentMap<String, Set<String>> permissions() {
        return this.permissionMap;
    }

    @Override
    public Set<String> permissions(final String profile) {
        return this.permissionMap.getOrDefault(profile, Set.of());
    }

    @Override
    public boolean match(final AuthorizationContext context) {
        if (SecuritySession.of().isDisabled403()) {
            // 如果授权没打开直接返回 true，跳过授权
            return true;
        }
        Objects.requireNonNull(context);
        final User user = context.user();
        if (user != null) {
            final Authorization resolved = ProfileAuthorization.create(this.permissionMap);
            return user.authorizations().verify(resolved);
        }
        return false;
    }

    @Override
    public boolean verify(final Authorization otherAuthorization) {
        if (SecuritySession.of().isDisabled403()) {
            // 如果授权没打开直接返回 true，跳过授权
            return true;
        }
        Objects.requireNonNull(otherAuthorization);
        if (!(otherAuthorization instanceof final ProfileAuthorization profileAuthorization)) {
            return false;
        }
        final Set<Boolean> authorized = profileAuthorization.permissions().keySet().stream().map(profileName -> {
            final Set<String> resourcePermissions = profileAuthorization.permissions(profileName);
            final Set<String> userPermissions = this.permissionMap.getOrDefault(profileName, Set.of());
            return userPermissions.containsAll(resourcePermissions);
        }).collect(Collectors.toSet());
        // 任何一个合法就算通过
        return authorized.stream().anyMatch(item -> item);
    }
}
