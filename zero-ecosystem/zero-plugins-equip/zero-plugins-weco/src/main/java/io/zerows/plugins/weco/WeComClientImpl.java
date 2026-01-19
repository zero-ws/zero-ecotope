package io.zerows.plugins.weco;

import io.r2mo.base.exchange.*;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoActionType;
import io.r2mo.xync.weco.WeCoConstant;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.spi.HPI;

import java.util.Map;
import java.util.Objects;

@Defer
class WeComClientImpl implements WeComClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();

    private final WeCoConfig config;
    private final Vertx vertx;

    WeComClientImpl(final Vertx vertx, final WeCoConfig config) {
        this.vertx = vertx;
        this.config = config;
    }

    @Override
    public Future<JsonObject> authUrl(final String redirectUri, final String state) {
        final JObject params = SPI.J();

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.WX_AUTH_URL.name(),
            WeCoConstant.HEADER_REDIRECT_URI, redirectUri,
            WeCoConstant.HEADER_STATE, state
        );

        return this.doExchange(params, headers);
    }

    @Override
    public Future<JsonObject> login(final String code) {
        // 放入 code，WaitSpring 会将其提取到 message payload 中
        final JObject params = SPI.J()
            .put("code", code);

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.WX_LOGIN_BY.name()
        );

        return this.doExchange(params, headers);
    }

    @Override
    public Future<JsonObject> qrCode(final String state) {
        final JObject params = SPI.J();

        final WeCoConfig.WeComCp wecomConfig = this.config.getWecomCp();

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.APP_AUTH_QR.name(),
            "expireSeconds", String.valueOf(wecomConfig.getExpireSeconds()),
            WeCoConstant.HEADER_REDIRECT_URI, wecomConfig.getUrlCallback()
        );
        // 状态统一专用（保证企微可登录成功的关键）
        params.put("content", state);

        return this.doExchange(params, headers);
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        // Payload 约定为 UUID
        final JObject params = SPI.J()
            .put("code", uuid);

        final Map<String, Object> headers = Map.of(
            "action", WeCoActionType.APP_STATUS.name()
        );

        return this.doExchange(params, headers);
    }

    private Future<JsonObject> doExchange(final JObject params, final Map<String, Object> headers) {
        // 1. 获取企微转换器
        final UniProvider.Wait<WeCoConfig.WeComCp> wait = UniProvider.waitFor(WeComWaitVertx::new);
        final WeCoConfig.WeComCp wecomConfig = this.config.getWecomCp();

        // 2. 转换标准对象
        final UniAccount account = wait.account(params, wecomConfig);
        final UniContext context = wait.context(params, wecomConfig);
        final UniMessage<String> message = wait.message(params, headers, wecomConfig);

        // 3. 获取底层 Provider (SPI ID: UNI_WECOM)
        final UniProvider provider = CC_PROVIDER.pick(() -> HPI.findOne(UniProvider.class, "UNI_WECOM"));

        // 4. 执行并返回
        final UniResponse response = provider.exchange(account, message, context);
        final JObject result = (JObject) response.content();
        return Future.succeededFuture(Objects.nonNull(result) ? result.data() : new JsonObject());
    }
}
