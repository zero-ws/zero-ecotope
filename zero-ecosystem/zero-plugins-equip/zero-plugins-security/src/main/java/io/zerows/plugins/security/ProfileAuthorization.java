package io.zerows.plugins.security;

import io.vertx.ext.auth.authorization.Authorization;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ProfileAuthorization extends Authorization {
    static ProfileAuthorization create(final Set<String> permissions) {
        return new ProfileAuthorizationImpl(permissions);
    }

    static ProfileAuthorization create(final String permission) {
        return new ProfileAuthorizationImpl(new HashSet<>() {
            {
                this.add(permission);
            }
        });
    }

    Set<String> permissions();
}
