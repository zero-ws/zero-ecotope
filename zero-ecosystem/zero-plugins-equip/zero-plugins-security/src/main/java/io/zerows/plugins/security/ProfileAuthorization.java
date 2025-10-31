package io.zerows.plugins.security;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authorization.Authorization;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ProfileAuthorization extends Authorization {
    static ProfileAuthorization create(final ConcurrentMap<String, Set<String>> permissions) {
        return new ProfileAuthorizationImpl(permissions);
    }

    static ProfileAuthorization create(final JsonObject profileJ) {
        // 构造 ProfileAuthorization
        final ConcurrentMap<String, Set<String>> profiles = new ConcurrentHashMap<>();
        Ut.<JsonArray>itJObject(profileJ,
            // 此处 function 是反向的，key 是 field，value 是 values
            (values, field) -> profiles.put(field, Ut.toSet(values)));
        return create(profiles);
    }

    ConcurrentMap<String, Set<String>> permissions();

    Set<String> permissions(String profile);
}
