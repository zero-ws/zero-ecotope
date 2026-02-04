package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.vertx.common.cache.AkkaOr;
import io.r2mo.vertx.common.cache.MemoAtSecurity;
import io.vertx.core.Future;
import io.zerows.support.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * 用户缓存适配器 \uD83D\uDD0C
 *
 * 本类是 UserCache 接口的 SPI 实现，负责处理用户登录态、令牌、验证码等安全数据的缓存。
 *
 * \uD83D\uDEA8 核心特殊性说明：
 *
 * 1. 异步转同步 (Async to Sync) \uD83D\uDD04
 *    本类设计通过 {@link io.vertx.core.Future#await()} 方法实现了【异步转同步】的关键特性。
 *    - 底层：依赖 Vert.x 的异步存储实现 (Redis/Memory)。
 *    - 接口：对接 io.r2mo.jaas.session.UserCache 的同步接口定义。
 *    - 目的：让同步业务代码能够透明地使用异步存储能力，屏蔽底层的 Future/Promise 复杂度。
 *
 * 2. 架构适配层 \uD83C\uDFD7️
 *    UserCache 接口位于基础抽象层，定义为标准的同步 API 以简化业务逻辑开发。
 *    为了不破坏顶层接口的同步契约，本类承担了"桥接器"的角色：
 *    call(Sync) -> [ AuthUserCache ] -> await(Async) -> Store(Async)
 *
 * 3. 使用场景与警告 ⚠️
 *    由于强制调用了 .await() 阻塞当前线程等待结果：
 *    - \uD83D\uDCA5 必须在【虚拟线程 (Virtual Thread)】上下文中调用。
 *    - ⚠️ 严禁在 EventLoop 线程中调用，否则会导致事件循环死锁。
 *    - 这是为 JDK 21+ 虚拟线程设计的高吞吐同步适配模式。
 *
 * 4. 缓存策略调整
 *    - MemoAtSecurity 工厂：快速返回缓存操作句柄。
 *    - 细粒度控制：put/find/remove 等原子操作均通过 await 等待完成。
 * </pre>
 */
@Slf4j
@SPID(priority = 207)
public class AsyncUserCache implements UserCache {

    private static final Cc<String, MemoAtSecurity> CC_FACTORY = Cc.openThread();

    private MemoAtSecurity factory() {
        return CC_FACTORY.pick(AuthMemoAtSecurity::new);
    }

    // -------------------------------------------------------------------------
    // 登录态存储 (UserContext & UserAt)
    // -------------------------------------------------------------------------

    /**
     * <pre>
     * 登录态缓存写入 (UserContext) \uD83D\uDCBE
     *
     * 将包含完整上下文的用户信息写入缓存。
     * 调用此方法会触发 {@link io.vertx.core.Future#await()}，请确保运行在虚拟线程中。
     *
     * 1. 存储 UserContext 对象。
     * 2. 建立由 ID -> 详情的索引。
     * </pre>
     *
     * @param context 用户上下文对象
     */
    @Override
    public Akka<UserContext> login(final UserContext context) {
        if (context == null) {
            return AkkaOr.of();
        }

        // 1. 直接获取缓存实例
        final var cache = this.factory().userContext();
        // 2. 阻塞等待写入完成
        return AkkaOr.of(cache.put(context.id().toString(), context)
            .compose(done -> this.cacheVector(context.logged()))
            .compose(stored -> Future.succeededFuture(context))
        );
    }

    /**
     * <pre>
     * 登录态缓存写入 (UserAt) \uD83D\uDCBE
     *
     * 将轻量级用户对象写入缓存。
     * </pre>
     *
     * @param userAt 用户核心数据
     */
    @Override
    public Akka<UserAt> login(final UserAt userAt) {
        if (userAt == null) {
            return AkkaOr.of();
        }

        final var cache = this.factory().userAt();
        return AkkaOr.of(cache.put(userAt.id().toString(), userAt)
            .compose(done -> this.cacheVector(userAt.logged()))
            .compose(stored -> Future.succeededFuture(userAt))
        );
    }

    /**
     * <pre>
     * 索引构建 (Vector) \uD83D\uDD17
     *
     * 建立多维度的查询索引，支持通过 username, email, mobile 等反查 UUID。
     * </pre>
     *
     * @param user 用户数据
     */
    private Future<Void> cacheVector(final MSUser user) {
        if (user == null) {
            return Future.succeededFuture();
        }
        final Set<String> idKeys = user.ids();
        final String uidStr = user.getId().toString();

        // 提取缓存实例，循环内直接调用
        final var vectorCache = this.factory().userVector();

        // 循环同步写入
        final List<Future<Kv<String, String>>> future = new ArrayList<>();
        idKeys.forEach(idKey -> future.add(vectorCache.put(idKey, uidStr)));
        return Fx.combineT(future)
            .compose(done -> Future.succeededFuture());
    }

    /**
     * <pre>
     * 注销登录 \uD83D\uDEAA
     *
     * 清除指定用户的缓存数据 (UserContext 和 UserAt)。
     * </pre>
     *
     * @param userId 用户ID
     */
    @Override
    public Akka<Void> logout(final UUID userId) {
        if (userId == null) {
            return AkkaOr.of();
        }
        final String uidStr = userId.toString();

        return AkkaOr.of(this.factory().userAt().remove(uidStr)
            .compose(removed -> this.factory().userContext().remove(uidStr))
            .compose(removed -> Future.succeededFuture())
        );
    }

    // -------------------------------------------------------------------------
    // 登录态查找
    // -------------------------------------------------------------------------

    /**
     * <pre>
     * 获取用户上下文 \uD83D\uDCC4
     *
     * 根据 ID 同步获取 UserContext。
     * </pre>
     *
     * @param id 用户ID
     * @return UserContext 用户上下文
     */
    @Override
    public Akka<UserContext> context(final UUID id) {
        if (id == null) {
            return AkkaOr.of();
        }
        return AkkaOr.of(this.factory().userContext().find(id.toString()));
    }

    /**
     * <pre>
     * 智能用户查找 (Smart Lookup) \uD83D\uDD0D
     *
     * 本方法支持混合模式查找，自动识别输入参数类型（ID 或 账号）。
     * 解决了前端可能传入 UUID 或 业务账号 (username/mobile/email) 的不确定性。
     *
     * \uD83D\uDD04 查找流程 (Priority Loop)：
     *
     * 1. 主键直连 (UUID Priority) \uD83C\uDD94
     *    优先尝试将输入字符串解析为 UUID 并直接查找。
     *    若解析成功且找到记录，则直接返回结果（高性能路径）。
     *
     * 2. 索引兜底 (Index Fallback) \uD83D\uDCC7
     *    若步骤 1 未命中（不是 UUID 或 ID 不存在），则将输入视为业务账号。
     *    通过 {@link MemoAtSecurity#userVector()} 索引反查对应的 UUID。
     *    最终递归调用本方法获取 {@link UserAt} 详情。
     * </pre>
     *
     * @param idOr 用户主键 ID 或 登录账号 (username/mobile/email)
     * @return UserAt 用户详情，若双重查找均未命中则返回 null
     */
    @Override
    public Akka<UserAt> find(final String idOr) {
        if (idOr == null) {
            return AkkaOr.of();
        }

        // 1. 查找索引
        return AkkaOr.of(this.find(UUID.fromString(idOr)).<Future<UserAt>>compose().compose(found -> {
            /*
             * 修复查找不到的问题，输入的值可能是 id 也可能是其他标识如
             * - username
             * - mobile
             * - email
             * 查找顺序
             * 1. 优先使用 id 查找
             * 2. 其次使用索引查找
             */
            if (Objects.nonNull(found)) {
                return Future.succeededFuture(found);
            }

            return this.factory().userVector().find(idOr).compose(uidStr -> {
                if (Objects.isNull(uidStr)) {
                    return Future.succeededFuture();
                }
                // 2. 递归查找详情
                return this.find(UUID.fromString(uidStr)).compose();
            });
        }));
    }

    /**
     * <pre>
     * ID 查找用户 \uD83D\uDD0D
     *
     * 直接根据 UUID 获取 UserAt。
     * </pre>
     *
     * @param id 用户ID
     * @return UserAt 用户详情
     */
    @Override
    public Akka<UserAt> find(final UUID id) {
        if (id == null) {
            return null;
        }

        return AkkaOr.of(this.factory().userAt().find(id.toString()));
    }

    // -------------------------------------------------------------------------
    // 验证码 / 会话
    // -------------------------------------------------------------------------

    /**
     * <pre>
     * 写入验证码缓存 \uD83D\uDD12
     *
     * 存储生成的验证码，并在配置的时间后过期。
     * </pre>
     *
     * @param generated 生成的 kv (key=consumer, value=code)
     * @param config    验证码配置 (包含过期时间)
     */
    @Override
    public Akka<Void> authorize(final Kv<String, String> generated, final CaptchaArgs config) {
        if (generated == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofAuthorize(config).put(generated.key(), generated.value())
            .map(done -> {
                log.info("[ ZERO ] 验证码缓存写入：Key = {}, Code = {}, expiredAt = {}",
                    generated.key(), generated.value(), config.duration());
                return null;
            })
        );
    }

    /**
     * <pre>
     * 读取验证码 \uD83D\uDD13
     *
     * 根据消费者标识获取存储的验证码。
     * </pre>
     *
     * @param consumerId 消费者标识
     * @param config     配置信息
     * @return String 验证码
     */
    @Override
    public Akka<String> authorize(final String consumerId, final CaptchaArgs config) {
        if (consumerId == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofAuthorize(config).find(consumerId));
    }

    /**
     * <pre>
     * 移除验证码 \uD83D\uDDD1\uFE0F
     *
     * 验证成功后手动移除验证码。
     * </pre>
     *
     * @param consumerId 消费者标识
     * @param config     配置信息
     */
    @Override
    public Akka<Void> authorizeKo(final String consumerId, final CaptchaArgs config) {
        if (consumerId == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofAuthorize(config).remove(consumerId)
            .map(done -> {
                log.info("[ ZERO ] 验证码缓存清除：Key = {}", consumerId);
                return null;
            })
        );

    }

    // -------------------------------------------------------------------------
    // 令牌管理
    // -------------------------------------------------------------------------

    /**
     * <pre>
     * 访问令牌绑定 (Access Token) \uD83C\uDFAB
     *
     * 建立 Token -> UserID 的映射关系。
     * </pre>
     *
     * @param token  访问令牌
     * @param userId 用户ID
     */
    @Override
    public Akka<Void> token(final String token, final UUID userId) {
        if (token == null || userId == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofToken().put(token, userId.toString())
            .map(done -> null)
        );
    }

    /**
     * <pre>
     * 访问令牌解析 \uD83D\uDD0E
     *
     * 根据 Token 反查 UserID。
     * </pre>
     *
     * @param token 访问令牌
     * @return UUID 用户ID
     */
    @Override
    public Akka<UUID> token(final String token) {
        if (token == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofToken().find(token)
            .map(uidStr -> uidStr == null ? null : UUID.fromString(uidStr))
        );
    }

    /**
     * <pre>
     * 访问令牌撤销 \uD83D\uDEAB
     *
     * 移除 Token 映射。
     * </pre>
     *
     * @param token 访问令牌
     * @return boolean 是否成功
     */
    @Override
    public Akka<Boolean> tokenKo(final String token) {
        if (token == null) {
            return AkkaOr.of(false);
        }

        return AkkaOr.of(this.factory().ofToken().remove(token)
            .map(done -> true)
        );
    }

    /**
     * <pre>
     * 刷新令牌绑定 (Refresh Token) \uD83D\uDD04
     *
     * 建立 Refresh Token -> UserID 的映射关系。
     * </pre>
     *
     * @param refreshToken 刷新令牌
     * @param userId       用户ID
     */
    @Override
    public Akka<Void> tokenRefresh(final String refreshToken, final UUID userId) {
        if (refreshToken == null || userId == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofRefresh().put(refreshToken, userId.toString())
            .map(v -> null)
        );
    }

    /**
     * <pre>
     * 刷新令牌解析 \uD83D\uDD0E
     *
     * 根据 Refresh Token 反查 UserID。
     * </pre>
     *
     * @param refreshToken 刷新令牌
     * @return UUID 用户ID
     */
    @Override
    public Akka<UUID> tokenRefresh(final String refreshToken) {
        if (refreshToken == null) {
            return AkkaOr.of();
        }

        return AkkaOr.of(this.factory().ofRefresh().find(refreshToken)
            .map(uidStr -> uidStr == null ? null : UUID.fromString(uidStr))
        );
    }

    /**
     * <pre>
     * 刷新令牌撤销 \uD83D\uDEAB
     *
     * 移除 Refresh Token 映射。
     * </pre>
     *
     * @param refreshToken 刷新令牌
     * @return boolean 是否成功
     */
    @Override
    public Akka<Boolean> tokenRefreshKo(final String refreshToken) {
        if (refreshToken == null) {
            return AkkaOr.of(false);
        }

        return AkkaOr.of(this.factory().ofRefresh().remove(refreshToken)
            .map(done -> true)
        );
    }
}

