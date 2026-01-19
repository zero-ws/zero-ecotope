package io.zerows.plugins.security.weco;

import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.weco.exception._81553Exception401WeComAuthFailure;
import io.zerows.support.Fx;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Queue
@Slf4j
public class ApiWeCpActor {

    @Inject
    private WeComStub comStub;

    @Address(ApiAddr.API_AUTH_WECOM_INIT)
    public Future<JsonObject> init(final String targetUrl) {

        return this.comStub.initialize(targetUrl).compose(identify -> {
            final JObject responseJ = identify.response();
            log.info("[ PLUG ] ( WeCo ) 状态信息：{}", responseJ.encode());
            return Future.succeededFuture(responseJ.data());
        });
    }

    @Address(ApiAddr.API_AUTH_WECOM_LOGIN)
    public Future<String> login(final String code, final String state) {
        // 1. 构造专用请求 (构造函数内自动校验 code 非空)
        final JsonObject params = new JsonObject()
            .put("code", code).put("state", state);
        log.info("[ PLUG ] ( WeCo ) 企微登录请求参数：{}", params.encode());
        final WeComLoginRequest request = new WeComLoginRequest(params);

        // 2. 业务校验 & 获取 UserID
        return this.comStub.validate(request).compose(validated -> {
            try {

                // 4. 重定向到目标地址
                final String url = validated.url();
                final String token = validated.token();
                final String page = url.contains("?") ? url + "&token=" + token : url + "?token=" + token;
                return Future.succeededFuture(page);
            } catch (final Throwable ex) {
                log.error(ex.getMessage(), ex);
                return Fx.failOut(_81553Exception401WeComAuthFailure.class);
            }
        });
    }

    @Address(ApiAddr.API_AUTH_WECOM_QRCODE)
    public Future<JsonObject> qrCode(final String state) {
        return this.comStub.getQrCode(state);
    }
}
