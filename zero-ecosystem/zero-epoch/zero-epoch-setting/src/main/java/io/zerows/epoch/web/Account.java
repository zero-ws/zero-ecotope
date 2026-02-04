package io.zerows.epoch.web;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * <pre>
 * 🟢 用户账户工具类
 *
 * 1. 🌐 全局说明：
 *    提供针对 Vert.x User 对象、Session 会话、Token 令牌的统一操作入口。
 *    作为 Web 层与底层安全模块（Zero Security）之间的适配器。
 *
 * 2. 🎯 核心功能：
 *    - 用户提取：从 User 对象中提取 ID、会话句柄。
 *    - 令牌管理：JWT Token 的生成、解析、字段提取。
 *    - 会话绑定：将 Vert.x Session 与 User Principal 绑定。
 *    - 数据转换：UserAt 领域对象与 JsonObject Principal 互转。
 * </pre>
 */
public class Account {

    /**
     * <pre>
     * 🟢 提取用户会话句柄
     *
     * 1. 🌐 使用场景：
     *    在经过认证（Authenticated）的请求处理中，通过 Vert.x 的 User 对象获取
     *    系统内部定义的 UserAt 会话操作句柄。
     *
     * 2. 🎯 作用：
     *    - 解析 User Principal 获取用户唯一标识（ID）。
     *    - 在全局用户会话池（UserSession）中查找对应的活跃会话。
     *
     * 3. ⚙️ 注意事项：
     *    - 若用户未登录、会话过期或被踢出，此方法可能返回 null。
     *    - 此方法是连接 Vert.x Web 层与内部业务逻辑层的桥梁。
     * </pre>
     *
     * @param user Vert.x Web 上下文中的 User 对象
     * @return UserAt 内部会话操作对象，若未找到则返回 null
     */
    public static Future<UserAt> userAt(final User user) {
        final String userId = userId(user);
        // 同步提取会话信息
        return UserSession.of().find(userId).compose();
    }

    /**
     * <pre>
     * 🟢 绑定 Session 到用户凭证
     *
     * 1. 🌐 使用场景：
     *    用户登录成功后，或请求通过认证过滤器时，将当前的 Web Session ID
     *    注入到 User 对象的 Principal 数据中。
     *
     * 2. 🎯 作用：
     *    - 修改 User 内部 Principal 结构（有副作用）。
     *    - 建立 User -> Session 的关联，通过 key = "session" 存储 Session ID。
     *
     * 3. ⚙️ 注意事项：
     *    - 此操作直接修改传入的 User 对象引用。
     * </pre>
     *
     * @param user    已通过认证的 Vert.x User 对象
     * @param session 当前 HTTP 请求关联的 Session 对象
     * @return 修改后的 User 对象（支持链式调用）
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
     * 🟢 反向构建 User 对象
     *
     * 1. 🌐 使用场景：
     *    在某些后台任务或模拟登录场景中，已知内部 UserAt 会话数据，
     *    需要构造一个标准的 Vert.x User 对象以适配 Web 组件接口。
     *
     * 2. 🎯 作用：
     *    - 将 UserAt 转换为 Principal JSON 数据。
     *    - 使用 Vert.x 的 User.create 工厂方法生成实例。
     *    - 恢复用户的认证状态上下文。
     * </pre>
     *
     * @param userAt 内部会话操作对象
     * @return 构造完成的 Vert.x User 实例；若输入无效则返回 null
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
     * 🟢 快速提取用户 ID
     *
     * 1. 🌐 使用场景：
     *    需要获取当前操作用户的唯一标识符，通常用于数据库查询 filter
     *    或日志记录。
     *
     * 2. 🎯 作用：
     *    - 安全地从 User Principal 中读取 "key" 字段。
     *    - 避免直接操作 JsonObject 带来的空指针风险。
     * </pre>
     *
     * @param user Vert.x User 对象
     * @return 用户 ID 字符串；若 User 为空或无 ID 则返回 null
     */
    public static String userId(final User user) {
        final JsonObject principal = user.principal();
        if (Ut.isNil(principal)) {
            return null;
        }
        if (principal.containsKey(KName.ID)) {
            return principal.getString(KName.ID);
        }
        // 遗留系统
        return principal.getString(KName.KEY);
    }

    /**
     * <pre>
     * 🟢 构造 Principal 数据结构
     *
     * 1. 🌐 使用场景：
     *    将内部领域模型（UserAt/MSUser）转换为符合 Vert.x 认证规范的
     *    JSON 数据格式（Principal）。
     *
     * 2. 🎯 作用：
     *    - 提取核心字段：用户名、密码、ID。
     *    - 注入扩展字段：Habitus（环境/租户）、Session 标识。
     *
     * 3. ⚙️ 注意事项：
     *    - 手动组装 JsonObject，确保关键字段（如 password）存在，
     *      防止认证处理器报错。
     *    - Habitus 目前暂时使用 User ID 占位，后续应根据多租户逻辑计算。
     * </pre>
     *
     * @param userAt 内部会话操作对象
     * @return Principal JSON 数据；若输入为空则返回 null
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
        final String id = user.getId().toString();
        principal.put(KName.ID, id);
        // 鉴于旧版标识基本信息，此处还需要执行 habitus 对应的数据计算，此处 habitus 是后续执行过程中的核心
        principal.put(KName.HABITUS, id);
        principal.put(KName.SESSION, id);
        return principal;
    }

    /**
     * <pre>
     * 🟢 规范化认证凭证数据
     *
     * 1. 🌐 使用场景：
     *    在接收到客户端提交的登录请求数据（User/Pass 或 Token）后，
     *    统一标准字段名称，以便后续认证处理器识别。
     *
     * 2. 🎯 作用：
     *    - 映射 "username" -> "session"。
     *    - 映射 "token" -> "session" 和 "access_token"。
     *    - 屏蔽不同登录方式（账号密码 vs 令牌）的参数差异。
     * </pre>
     *
     * @param credentials Vert.x 认证凭证接口对象
     * @return 处理后的 JSON 格式凭证数据
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
     * 🟢 提取用户上下文（预留）
     *
     * 1. 🌐 使用场景：
     *    设计用于提取更丰富的用户上下文信息（如角色、权限列表等）。
     *
     * 2. 🎯 作用：
     *    - 当前版本尚未实现。
     *    - 返回 null。
     * </pre>
     *
     * @param user Vert.x User 对象
     * @return UserContext 上下文对象
     */
    public static UserContext userContext(final User user) {
        return null;
    }
    // ------------------------- Token 相关

    /**
     * <pre>
     * 🟢 解码 Token 载荷（Payload）
     *
     * 1. 🌐 使用场景：
     *    收到 HTTP 请求中的 Token 字符串（无 Bearer 前缀）时，
     *    直接解析其内部包含的业务数据。默认按 JWT 格式处理。
     *
     * 2. 🎯 作用：
     *    - 调用底层 Token 编解码器反序列化 Token。
     *    - 获取包含 sub, iat, exp 等标准 Claim 的 JSON 数据。
     *    - 支持提取扩展字段如 eff (有效期), loginType (登录方式)。
     *
     * 3. ⚙️ 功能特性：
     *    - 方法名为 userToken，意为从 Token 中还原用户信息。
     *    - 若解析失败或 Token 无效，行为取决于底层实现（通常抛出异常或返回 null）。
     * </pre>
     *
     * @param token 原始 Token 字符串（例如 JWT 的三个部分）
     * @return Token 载荷数据（JsonObject）
     */
    public static JsonObject userToken(final String token) {
        return tokenOf().decode(token);
    }

    /**
     * <pre>
     * 🟢 生成 Token 字符串
     *
     * 1. 🌐 使用场景：
     *    用户登录成功后，需要为客户端签发访问令牌（Access Token）。
     *    输入包含用户信息的 JSON 数据，输出最终的 Token 字符串。
     *
     * 2. 🎯 作用：
     *    - 使用默认算法（通常为 JWT）对数据进行签名和编码。
     *    - 生成的 Token 包含输入数据作为 Payload。
     *
     * 3. ⚙️ 数据结构说明：
     *    建议输入数据 tokenData 包含以下标准与扩展字段：
     *    - sub (Subject): 用户唯一标识（必需）。
     *    - iat (Issued At): 签发时间（毫秒/秒）。
     *    - exp (Expiration): 过期时间（毫秒/秒）。
     *    - eff (Effective): 有效期（兼容旧版或特定框架字段）。
     *    - loginType: 登录类型（如 "R2MO-SA-TOKEN"）。
     *    以及其他业务相关的扩展字段。
     * </pre>
     *
     * @param tokenData 需要封装到 Token 中的载荷数据
     * @return 签名后的 Token 字符串；若数据为空则返回 null
     */
    public static String userToken(final JsonObject tokenData) {
        if (Ut.isNil(tokenData)) {
            return null;
        }
        return tokenOf().encode(tokenData);
    }

    /**
     * <pre>
     * 🟢 解析 Authorization 头
     *
     * 1. 🌐 使用场景：
     *    处理 HTTP 请求头 "Authorization" 的完整内容。
     *    支持 "Bearer <token>"（JWT）或 "Basic <token>"（AES/Base64）等格式。
     *
     * 2. 🎯 作用：
     *    - 自动识别 Token 类型（JWT/AES/Basic）。
     *    - 剥离前缀（如 "Bearer "），提取核心 Token 串。
     *    - 解码并返回 Payload 数据。
     *
     * 3. ⚙️ 处理逻辑：
     *    - 根据 Authorization 字符串格式判断 TokenType。
     *    - 路由到对应的 Token 处理器进行解码。
     * </pre>
     *
     * @param authorization 完整的 HTTP Authorization 头值
     * @return 解析后的 Payload 数据；若输入为空则返回 null
     */
    public static JsonObject userAuthorization(final String authorization) {
        if (Ut.isNil(authorization)) {
            return null;
        }
        final TokenType detected = TokenType.fromString(authorization);
        final String token = authorization.split(" ")[1];
        return Token.of(detected).decode(token);
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
     * 🟢 读取 Token 指定字段
     *
     * 1. 🌐 使用场景：
     *    只需要 Token 中的某个特定值（如仅需要获取 User ID "sub"），
     *    而不需要完整的 JsonObject。
     *
     * 2. 🎯 作用：
     *    - 先解码 Token 获取完整 Payload。
     *    - 安全提取指定字段的字符串值。
     *
     * 3. ⚙️ 便捷性：
     *    - 避免了调用方重复编写解析和空值检查代码。
     *    - 常用于快速提取 sub, aud, iss, loginType 等字段。
     * </pre>
     *
     * @param token 原始 Token 字符串
     * @param field 需要提取的 Payload 字段名
     * @return 字段值字符串；若字段不存在或解析失败返回 null
     */
    public static String userToken(final String token, final String field) {
        final JsonObject userJ = userToken(token);
        return Ut.valueString(userJ, field);
    }
}
