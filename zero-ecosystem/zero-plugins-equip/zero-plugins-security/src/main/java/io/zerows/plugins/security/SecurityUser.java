package io.zerows.plugins.security;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.constant.KName;

import java.util.Objects;

/**
 * 此方法和 {@see io.r2mo.spring.security.SecurityUser} 同名，主要目的在于 spring 和 vertx 不可能同时共存于同一个项目，这是使用
 * Zero 的一个禁忌，二者必须是二选一，所以同名不会造成歧义，相反让开发人员在不同框架下使用同样的类名会更方便记忆。
 */
public class SecurityUser {

    public static Future<UserAt> loggedAsync() {
        return null;
    }

    public static UserAt logged() {
        return null;
    }

    public static Future<UserContext> contextAsync() {
        return null;
    }

    public static UserContext context() {
        return null;
    }

    public static Future<String> idAsync() {
        return idAsync(false);
    }

    public static String id() {
        return id(false);
    }

    public static <T> Future<T> idAsync(final boolean isUuid) {
        return null;
    }

    public static <T> T id(final boolean isUuid) {
        return null;
    }

    // --------------- User 相关方法 ---------------

    public static JsonObject toJObject(final Credentials credentials) {
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

    public static User toUser(final UserAt userAt) {
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
}
