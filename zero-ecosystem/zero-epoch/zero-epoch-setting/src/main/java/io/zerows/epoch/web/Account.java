package io.zerows.epoch.web;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.jaas.session.UserSession;
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

    /**
     * <pre>
     * 🟢 提取用户会话
     *
     * 1. 🌐 使用场景：
     *    在 Authenticated 的请求中，根据 `User` 对象获取 `UserAt` (会话操作句柄)。
     *    用于后续的权限验证或用户信息获取。
     *
     * 2. 🎯 作用：
     *    - 从 `User` 中提取 `userId`。
     *    - 根据 `userId` 从 `UserSession` 中查找活跃会话。
     *
     * 3. ⚙️ 注意：
     *    - 如果用户未登录或 Session 过期，可能返回 null。
     * </pre>
     *
     * @param user Vert.x Web 上下文中的 User 对象
     * @return UserAt 会话操作对象
     */
    public static UserAt userAt(final User user) {
        final String userId = userId(user);
        return UserSession.of().find(userId);
    }

    /**
     * <pre>
     * 🟢 注入 Session ID 到 Principal (带副作用)
     *
     * 1. 🌐 使用场景：
     *    在认证成功后，将当前的 Web Session ID 绑定到 User 的 Principal 中。
     *    使得 User 对象携带 Session 信息。
     *
     * 2. 🎯 作用：
     *    - 这里修改了 `user.principal()` 的内容。
     *    - 将 `KName.SESSION` 字段设置为 `session.id()`。
     *
     * 3. ⚙️ 注意：
     *    - 这是一个由副作用的方法，会直接修改参数对象。
     * </pre>
     *
     * @param user    认证后的用户对象
     * @param session 当前的 Web Session
     * @return 修改后的 User 对象
     */
    public static User userVx(final User user, final Session session) {
        // 引用提取，带副作用
        final JsonObject principal = user.principal();
        if (Objects.nonNull(principal) && Objects.nonNull(session)) {
            principal.put(KName.SESSION, session.id());
        }
        return user;
    }

    /**
     * <pre>
     * 🟢 构建 Vert.x User 对象
     *
     * 1. 🌐 使用场景：
     *    根据内部的 `UserAt` 会话对象，反向构建 Vert.x 的 `User` 对象。
     *    通常用于手动模拟登录状态或恢复上下文。
     *
     * 2. 🎯 作用：
     *    - 提取 `UserAt` 中的身份信息。
     *    - 构造 Principal (JSON)。
     *    - 创建 Vert.x 的 User 实例。
     * </pre>
     *
     * @param userAt 内部会话对象
     * @return Vert.x User 实例
     */
    public static User userVx(final UserAt userAt) {
        final JsonObject principal = userData(userAt);
        if (Objects.isNull(principal)) {
            return null;
        }
        /*
         * 后续处理，加载用户信息
         */
        return User.create(principal, userAt.data().data());
    }

    /**
     * <pre>
     * 🟢 提取用户 ID
     *
     * 1. 🌐 使用场景：
     *    从 Vert.x 的 `User` 对象中快速获取用户唯一标识 (ID)。
     *
     * 2. 🎯 作用：
     *    - 解析 principal JSON。
     *    - 返回 `KName.ID` 字段。
     * </pre>
     *
     * @param user Vert.x 用户对象
     * @return 用户 ID 字符串
     */
    public static String userId(final User user) {
        final JsonObject principal = user.principal();
        if (Ut.isNil(principal)) {
            return null;
        }
        return principal.getString(KName.ID);
    }

    /**
     * <pre>
     * 🟢 构造用户 Principal 数据
     *
     * 1. 🌐 使用场景：
     *    将内部领域模型 `UserAt` 转换为 JSON 格式的 Principal 数据。
     *    用于 Vert.x 认证系统的数据交换。
     *
     * 2. 🎯 作用：
     *    - 提取 Username, Password, ID。
     *    - 注入 Habitus (租户/环境信息)。
     *    - 注入 Session 标识。
     *
     * 3. ⚙️ 逻辑：
     *    - 手动组装 JsonObject 以避免字段为 null 导致的异常。
     * </pre>
     *
     * @param userAt 内部会话对象
     * @return Principal JSON 数据
     */
    public static JsonObject userData(final UserAt userAt) {
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
        principal.put(KName.SESSION, user.getUsername());
        return principal;
    }

    /**
     * <pre>
     * 🟢 凭证数据预处理
     *
     * 1. 🌐 使用场景：
     *    在认证过程中，处理客户端提交的 `Credentials`。
     *    标准化其中的字段名称。
     *
     * 2. 🎯 作用：
     *    - 将 `username` 映射为 `session`。
     *    - 将 `token` 映射为 `session` 和 `access_token`。
     *    - 统一不同认证方式的字段差异。
     * </pre>
     *
     * @param credentials 用户提交的凭证
     * @return 处理后的 JSON 数据
     */
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

    /**
     * <pre>
     * 🟢 提取用户上下文 (保留接口)
     *
     * 1. 🌐 使用场景：
     *    预留接口，用于将来从 User 对象中提取更复杂的上下文信息。
     *
     * 2. 🎯 作用：
     *    - 目前暂未实现，直接返回 null。
     * </pre>
     *
     * @param user Vert.x 用户对象
     * @return 用户上下文
     */
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
        return tokenOf().decode(token);
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
        return tokenOf().encode(tokenData);
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

    private static Token tokenOf() {
        Token token = Token.of(TokenType.JWT);
        if (Objects.isNull(token)) {
            token = Token.of(TokenType.AES);
        }
        return token;
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
