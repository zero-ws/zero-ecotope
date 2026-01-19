package io.zerows.plugins.weco;

import io.r2mo.base.exchange.*;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoActionType;
import io.r2mo.xync.weco.WeCoConstant;
import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.support.Ut;

import java.util.Map;
import java.util.Objects;

@Defer
class WeChatClientImpl implements WeChatClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    private final WeCoConfig config;
    private final Vertx vertx;

    WeChatClientImpl(final Vertx vertx, final WeCoConfig config) {
        this.vertx = vertx;
        this.config = config;
    }

    public Vertx vertx() {
        return this.vertx;
    }

    @Override
    public Future<JsonObject> authUrl(final String redirectUri, final String state) {
        // 1. 准备参数 (无需 Payload)
        final JObject params = SPI.J();

        // 2. 设置头部指令
        // 告诉 Provider 执行 "获取认证URL" 操作，并传递必要参数
        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.WX_AUTH_URL.name(),
            WeCoConstant.HEADER_REDIRECT_URI, redirectUri,
            WeCoConstant.HEADER_STATE, state
        );

        return this.doExchangeMp(params, headers);
    }

    @Override
    public Future<JsonObject> login(final String code) {
        // 1. 准备参数 (Payload 为 code)
        final JObject params = SPI.J()
            .put("code", code);

        // 2. 设置头部指令 (执行登录)
        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.WX_LOGIN_BY.name()
        );

        return this.doExchangeMp(params, headers);
    }

    @Override
    public Future<JsonObject> qrCode() {
        final JObject params = SPI.J();

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.APP_AUTH_QR.name(),
            "expireSeconds", String.valueOf(this.config.getWechatMp().getExpireSeconds())
        );

        return this.doExchangeMp(params, headers);
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        // Payload 约定为 UUID (对应 WeCoActionStatus 的 request.payload())
        // WeCoBuilder 会将 "code" 或 "content" 作为 Payload
        final JObject params = SPI.J()
            .put("code", uuid);

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.APP_STATUS.name(),
            "expireSeconds", String.valueOf(this.config.getWechatMp().getExpireSeconds())
        );

        return this.doExchangeMp(params, headers);
    }

    @Override
    public Future<Boolean> checkEcho(final WeArgsSignature params) {
        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.APP_PRE.name()
        );
        return this.doExchangeMp(params.build(), headers)
            .map(checked -> Ut.valueT(checked, "success", Boolean.class));
    }

    @Override
    public Future<JsonObject> extractUser(final WeArgsCallback parameter) {
        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.LOGGED_USER.name(),
            "expireSeconds", String.valueOf(this.config.getWechatMp().getExpireSeconds())
        );
        // content -> parameter
        return this.doExchangeMp(parameter.message(), headers);
    }

    /**
     * 核心交换逻辑
     */
    private Future<JsonObject> doExchangeMp(final JObject params, final Map<String, Object> headers) {
        // 1. 获取转换器
        final UniProvider.Wait<WeCoConfig.WeChatMp> wait = UniProvider.waitFor(WeChatMPWaitVertx::new);
        final WeCoConfig.WeChatMp wechatConfig = this.config.getWechatMp();

        // 2. 转换为标准对象 (Account, Context, Message)
        final UniAccount account = wait.account(params, wechatConfig);
        final UniContext context = wait.context(params, wechatConfig);
        final UniMessage<String> message = wait.message(params, headers, wechatConfig);

        // 3. 获取底层 Provider (SPI ID: UNI_WECHAT)
        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_WECHAT"));

        // 4. 执行并返回
        final UniResponse response = provider.exchange(account, message, context);
        final JObject result = (JObject) response.content();
        return Future.succeededFuture(Objects.nonNull(result) ? result.data() : new JsonObject());
    }
}
