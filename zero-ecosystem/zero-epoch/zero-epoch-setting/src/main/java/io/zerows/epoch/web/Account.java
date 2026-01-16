package io.zerows.epoch.web;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.SecurityType;
import io.zerows.support.Ut;

import java.util.Objects;

public class Account {
    
    public static UserAt userAt() {
        return null;
    }

    public static UserAt userAt(final User user) {
        return null;
    }

    public static User userVx(final User user, final Session session) {
        // 引用提取，带副作用
        final JsonObject principal = user.principal();
        if (Objects.nonNull(principal) && Objects.nonNull(session)) {
            principal.put(KName.SESSION, session.id());
        }
        return user;
    }

    public static User userVx(final UserAt userAt) {
        if (Objects.isNull(userAt)) {
            return null;
        }
        final MSUser user = userAt.logged();
        if (Objects.isNull(user)) {
            return null;
        }
        /*
         * 构造身份主体 Principal 信息，此处手动组装 JsonObject，防止 password cannot be null 的错误
         *
         */
        final JsonObject principal = new JsonObject();
        principal.put(KName.USERNAME, user.getUsername());
        principal.put(KName.PASSWORD, user.getPassword());
        principal.put(KName.ID, user.getId().toString());
        // 鉴于旧版标识基本信息，此处还需要执行 habitus 对应的数据计算，此处 habitus 是后续执行过程中的核心
        principal.put(KName.HABITUS, user.getId().toString());
        final User authUser = User.create(principal, userAt.data().data());
        /*
         * 后续处理，加载用户信息
         */
        return authUser;
    }

    public static <T> T userId(final boolean isUuid) {
        return null;
    }

    public static String userId(final User user) {
        return null;
    }

    public static JsonObject userToken(final String token) {
        if (Ut.isNil(token)) {
            return null;
        }
        return null;
    }

    public static JsonObject userToken(final String token, final SecurityType type) {
        if (Ut.isNil(token)) {
            return null;
        }
        return null;
    }

    public static String userToken(final JsonObject tokenData) {
        if (Ut.isNil(tokenData)) {
            return null;
        }
        return null;
    }

    public static String userToken(final String token, final String field) {
        final JsonObject userJ = userToken(token);
        return Ut.valueString(userJ, field);
    }

    public static JsonObject userData(final Credentials credentials) {
        final JsonObject authJson = credentials.toJson();
        if (authJson.containsKey(KName.USERNAME)) {
            // username -> session
            authJson.put(KName.SESSION, authJson.getString(KName.USERNAME));
        }
        if (authJson.containsKey(KName.TOKEN)) {
            // token -> session
            authJson.put(KName.SESSION, authJson.getString(KName.TOKEN));
            // token -> access_token
            authJson.put(KName.ACCESS_TOKEN, authJson.getString(KName.TOKEN));
        }
        return authJson;
    }

    public static UserContext userContext() {
        return null;
    }
}
