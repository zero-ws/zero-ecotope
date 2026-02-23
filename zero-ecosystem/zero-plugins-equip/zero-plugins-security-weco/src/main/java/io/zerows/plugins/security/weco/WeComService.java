package io.zerows.plugins.security.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.wecom.WeComIdentify;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KRef;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import io.zerows.plugins.security.weco.exception._81552Exception501WeComDisabled;
import io.zerows.plugins.security.weco.exception._81553Exception401WeComAuthFailure;
import io.zerows.plugins.security.weco.exception._81554Exception401WeComBlocked;
import io.zerows.plugins.weco.WeCoActor;
import io.zerows.plugins.weco.WeCoAsyncSession;
import io.zerows.plugins.weco.WeComClient;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.support.Fx;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class WeComService implements WeComStub {
    private final WeCoConfig.WeComCp config;
    @Inject
    private WeComClient weComClient;
    @Inject
    private AuthLoginStub authStub;

    public WeComService() {
        this.config = WeCoActor.configOfWeComCp();
    }

    @Override
    public Future<WeComIdentify> initialize(final String targetUrl) {
        this.checkEnabled();
        // 黑名单
        final String host = URI.create(targetUrl).getHost();
        if (Objects.isNull(host) || this.config.getBlockDomains().contains(host)) {
            return Fx.failOut(_81554Exception401WeComBlocked.class, targetUrl);
        }
        // 生成流程 ID（state）
        final String state = UUID.randomUUID().toString().replace("-", "");
        final WeComIdentify identify = new WeComIdentify()
            .state(state)
            .url(targetUrl);
        // 缓存会话信息
        final String sessionKey = WeCoSession.keyOf(state);
        return WeCoAsyncSession.of().saveAsync(
            sessionKey, identify.cached(),
            Duration.ofSeconds(this.config.getExpireSeconds())  // 默认5分钟
        ).map(initialized -> {
            log.info("[ ZERO ] ( WeCo ) 企微登录初始化完成，sessionKey = {}, state = {}", sessionKey, state);
            return identify;
        });
    }

    @Override
    public Future<WeComIdentify> validate(final WeComLoginRequest request) {
        this.checkEnabled();
        final String code = request.getCode();

        final KRef ref = new KRef();
        // 1. 远程换取 Token/Info
        return this.weComClient.login(code).compose(result -> {

            // 2. 提取关键信息 (UserID 或 OpenID)
            // 企微逻辑：优先取 UserId (内部成员)，取不到则取 OpenId (外部联系人)
            String userId = result.getString("userId");
            if (userId == null) {
                userId = result.getString("openId");
            }

            // 参照 WeChat 实现，此处校验失败抛出 501 Disabled 类型异常
            if (StrUtil.isEmpty(userId)) {
                return Fx.failOut(_81553Exception401WeComAuthFailure.class);
            }

            // 3. 填充身份标识
            // 这里会自动联动设置父类的 id = userId
            request.setUserId(userId);
            ref.add(userId);

            return this.authStub.login(request);
        }).compose(userAt -> {
            if (Objects.isNull(userAt)) {
                return Fx.failOut(_81553Exception401WeComAuthFailure.class);
            }
            final TokenDynamicResponse response = new TokenDynamicResponse(userAt);
            final String token = response.getToken();
            if (StrUtil.isEmpty(token)) {
                return response.getTokenAsync().compose();
            } else {
                return Future.succeededFuture(token);
            }
        }).compose(token -> {
            if (StrUtil.isEmpty(token)) {
                return Fx.failOut(_81553Exception401WeComAuthFailure.class);
            }
            log.info("[ ZERO ] ( WeCo ) WeCom 认证通过, UserID: {}", (String) ref.get());
            final String sessionKey = WeCoSession.keyOf(request.getState());
            return WeCoAsyncSession.of().getAsync(sessionKey, Duration.ofSeconds(this.config.getExpireSeconds())).compose(cached -> {
                final WeComIdentify identify = new WeComIdentify(cached);
                identify.token(token);
                return Future.succeededFuture(identify);
            });
        });
    }

    @Override
    public Future<JsonObject> getQrCode(final String state) {
        this.checkEnabled();
        return this.weComClient.qrCode(state);
    }

    private void checkEnabled() {
        Fn.jvmKo(Objects.isNull(this.config), _81552Exception501WeComDisabled.class);
    }
}
