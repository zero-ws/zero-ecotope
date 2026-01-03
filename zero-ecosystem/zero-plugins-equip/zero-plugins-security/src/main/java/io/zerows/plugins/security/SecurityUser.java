package io.zerows.plugins.security;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.vertx.core.Future;

/**
 * 此方法和 {@see io.r2mo.spring.security.SecurityUser} 同名，主要目的在于 spring 和 vertx 不可能同时共存于同一个项目，这是使用
 * Zero 的一个禁忌，二者必须是二选一，所以同名不会造成歧义，相反让开发人员在不同框架下使用同样的类名会更方便记忆。
 */
public class SecurityUser {

    public static Future<UserAt> logged() {
        return null;
    }

    public static Future<UserContext> context() {
        return null;
    }

    public static Future<String> id() {
        return id(false);
    }

    public static <T> Future<T> id(final boolean isUuid) {
        return null;
    }
}
