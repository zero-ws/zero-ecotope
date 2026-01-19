package io.zerows.plugins.security.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.xync.weco.WeCoSession;
import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import io.zerows.plugins.security.weco.exception._81502Exception501WeChatDisabled;
import io.zerows.plugins.security.weco.exception._81503Exception401WeChatAuthFailure;
import io.zerows.plugins.weco.WeChatClient;
import io.zerows.plugins.weco.WeCoActor;
import io.zerows.plugins.weco.WeCoAsyncSession;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.support.Fx;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class WeChatService implements WeChatStub {

    private final WeCoConfig.WeChatMp configMp;
    private final WeCoConfig.WeChatOpen configOpen;

    @Inject
    private WeChatClient weChatClient;

    @Inject
    private AuthLoginStub authStub;

    public WeChatService() {
        this.configMp = WeCoActor.configOfWeChatMp();
        this.configOpen = WeCoActor.configOfWeChatOpen();
    }

    @Override
    public Future<JsonObject> getAuthUrl(final String redirectUri, final String state) {
        // 1. 确认开启模块
        this.enabledOpen();
        // 2. 获取授权 URL
        return this.weChatClient.authUrl(redirectUri, state);
    }

    @Override
    public Future<WeChatReqPreLogin> validate(final WeChatReqPreLogin request) {
        this.enabledOpen();

        final String code = request.getCode();

        // 1. 远程换取 Token/Info
        return this.weChatClient.login(code).compose(result -> {
            // 2. 提取关键信息 (OpenID)
            final String openId = result.getString("openid");
            if (StrUtil.isEmpty(openId)) {
                return Fx.failOut(_81503Exception401WeChatAuthFailure.class);
            }

            // 3. 填充身份标识
            // 这里会自动联动设置父类的 id = openId
            request.setOpenId(openId);
            request.setUnionId(result.getString("unionid")); // 只有绑定了开放平台才有

            log.info("[ ZERO ] ( WeChat ) 认证通过, OpenID: {}", openId);
            return Future.succeededFuture(request);
        });
    }

    @Override
    public Future<JsonObject> getQrCode() {
        this.enabledMp();
        return this.weChatClient.qrCode();
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        this.enabledMp();
        return this.weChatClient.checkStatus(uuid);
    }

    @Override
    public Future<Boolean> checkEcho(final WeArgsSignature params) {
        this.enabledMp();
        return this.weChatClient.checkEcho(params);
    }

    @Override
    public Future<JsonObject> extract(final String uuid, final WeArgsCallback parameter) {
        this.enabledMp();
        return this.weChatClient.extractUser(parameter).compose(checked -> {
            // 提取用户信息生成 LoginRequest
            final WeChatReqAccount request = new WeChatReqAccount(checked);
            return this.authStub.login(request);
        }).compose(userAt -> {
            final TokenDynamicResponse response = new TokenDynamicResponse(userAt);
            // 更新 token 保证 WeCoSession 中的 /wechat-status 能检查到最新的 Token
            final String sessionKey = WeCoSession.keyOf(uuid);
            final Duration expiredAt = Duration.ofSeconds(this.configMp.getExpireSeconds());
            return WeCoAsyncSession.of().saveAsync(sessionKey, response.getToken(), expiredAt).compose(checked -> {
                // 响应信息
                final JsonObject responseJ = new JsonObject();
                responseJ.put(KName.ID, userAt.id().toString());
                responseJ.put(KName.TOKEN, response.getToken());
                return Future.succeededFuture(responseJ);
            });
        });
    }

    private void enabledOpen() {
        Fn.jvmKo(Objects.isNull(this.configOpen), _81502Exception501WeChatDisabled.class);
    }

    private void enabledMp() {
        Fn.jvmKo(Objects.isNull(this.configMp), _81502Exception501WeChatDisabled.class);
    }
}
