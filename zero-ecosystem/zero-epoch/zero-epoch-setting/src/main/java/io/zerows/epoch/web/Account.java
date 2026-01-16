package io.zerows.epoch.web;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.SecurityType;
import io.zerows.support.Ut;

import java.util.Objects;

public class Account {

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

    public static UserContext userContext(final User user) {
        return null;
    }
    // ------------------------- Token 相关

    /**
     * <pre>
     * 🟢 提取 Token 载荷（默认为 JWT）
     *
     * 1. 🌐 使用场景：
     *    将原始 Token 字符串（通常来自 HTTP 头 `Authorization`）传入此方法。
     *    它会直接将 Token 字符串解码为 `JsonObject`。
     *
     * 2. 🎯 作用：
     *    - 将 Token 字符串解析为 JsonObject。
     *    - 默认用于 `SecurityType.JWT` 类型。
     *
     * 3. ⚙️ 功能示例：
     *    JsonObject payload = Account.userToken("eyJhbGci...");
     * </pre>
     *
     * @param token 原始 Token 字符串
     * @return Token 的有效载荷（Payload）
     */
    public static JsonObject userToken(final String token) {
        return userToken(token, SecurityType.JWT);
    }

    /**
     * <pre>
     * 🟢 生成 Token 字符串（JWT）
     *
     * 1. 🌐 使用场景：
     *    将载荷数据（JsonObject）传入此方法。
     *    它会将数据编码为 Token 字符串（JWT）。
     *
     * 2. 🎯 作用：
     *    - 根据数据生成 Token 字符串。
     *    - 用于向客户端签发 Token。
     *
     * 3. ⚙️ 功能示例：
     *    String token = Account.userToken(new JsonObject().put("id", "user-id"));
     * </pre>
     *
     * @param tokenData Token 载荷数据
     * @return 生成的 Token 字符串
     */
    public static String userToken(final JsonObject tokenData) {
        if (Ut.isNil(tokenData)) {
            return null;
        }
        return Token.of(TokenType.JWT).encode(tokenData);
    }

    /**
     * <pre>
     * 🟢 提取 Token 载荷（指定类型）
     *
     * 1. 🌐 使用场景：
     *    将原始 Token 字符串连同 `SecurityType` 传入此方法。
     *    它会根据类型将 Token 字符串解码为 `JsonObject`。
     *
     * 2. 🎯 作用：
     *    - 将 Token 字符串解析为 JsonObject。
     *    - 支持 `SecurityType.BASIC` (AES) 和其他类型 (JWT)。
     *
     * 3. ⚙️ 功能示例：
     *    JsonObject payload = Account.userToken("...", SecurityType.BASIC);
     * </pre>
     *
     * @param token 原始 Token 字符串
     * @param type  安全类型
     * @return Token 的有效载荷（Payload）
     */
    public static JsonObject userToken(final String token, final SecurityType type) {
        if (Ut.isNil(token)) {
            return null;
        }
        if (SecurityType.BASIC == type) {
            return Token.of(TokenType.AES).decode(token);
        } else {
            return Token.of(TokenType.JWT).decode(token);
        }
    }

    /**
     * <pre>
     * 🟢 提取 Token 字段
     *
     * 1. 🌐 使用场景：
     *    将原始 Token 字符串和字段名传入此方法。
     *    它会从 Token 载荷中提取该字段的值。
     *
     * 2. 🎯 作用：
     *    - 直接从 Token 中获取特定值。
     *    - 简化代码结构，无需额外的变量定义。
     *
     * 3. ⚙️ 功能示例：
     *    String userId = Account.userToken("...", "sub");
     * </pre>
     *
     * @param token 原始 Token 字符串
     * @param field 载荷中的字段名
     * @return 字段的值
     */
    public static String userToken(final String token, final String field) {
        final JsonObject userJ = userToken(token);
        return Ut.valueString(userJ, field);
    }
}
