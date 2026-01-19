package io.zerows.plugins.sms;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Defer
public class SmsClientImpl implements SmsClient {

    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();
    private final Vertx vertx;
    private final SmsConfig config;

    SmsClientImpl(final Vertx vertx, final SmsConfig config) {
        this.vertx = vertx;
        this.config = config;
    }

    public Vertx vertx() {
        return this.vertx;
    }

    @Override
    public Future<JsonObject> sendAsync(final String template, final JsonObject paramsJ, final Set<String> toSet) {
        final JObject params = Ut.valueJ(paramsJ);
        params.put("template", template);
        // 1. 根据配置发送邮件
        final UniProvider.Wait<SmsConfig> wait = UniProvider.waitFor(SmsWaitVertx::new);
        final UniAccount account = wait.account(params, this.config);
        final UniContext context = wait.context(params, this.config);

        // 2. 消息构造
        final UniMessage<String> message = wait.message(params, this.config);
        toSet.forEach(message::addTo);

        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_SMS"));
        final String result = provider.send(account, message, context);
        final JObject sentJ = UniProvider.replySuccess(result);
        log.info("[ PLUG ] ( SMS ) 发送短信完成，结果：{}", sentJ);
        return Future.succeededFuture(sentJ.data());
    }

//    public SmsClient send(final String mobile, final String tplCode, final JsonObject params,
//                          final Handler<AsyncResult<JsonObject>> handler) {
//        final SendSmsRequest request = this.getRequest(mobile, this.config.getTpl(tplCode), params);
//        handler.handle(this.getResponse(request));
//        return this;
//    }

//    private Future<JsonObject> getResponse(final SendSmsRequest request) {
//        try {
//            final SendSmsResponse response = this.client.getAcsResponse(request);
//            final JsonObject data = new JsonObject();
//            data.put(SmsConfig.RESPONSE_REQUEST_ID, response.getRequestId());
//            data.put(SmsConfig.RESPONSE_BUSINESS_ID, response.getBizId());
//            data.put(SmsConfig.RESPONSE_CODE, response.getCode());
//            data.put(SmsConfig.RESPONSE_MESSAGE, response.getMessage());
//            log.info("[ PLUG ] 响应信息, code = {}, message = {}", response.getCode(), response.getMessage());
//            return Future.succeededFuture(data);
//        } catch (final ClientException ex) {
//            log.info(ex.getMessage(), ex);
//            return Fx.failOut(_20004Exception424MessageSend.class, ex);
//        }
//    }
//
//    private SendSmsRequest getRequest(final String mobile, final String tplCode, final JsonObject params) {
//        final SendSmsRequest request = new SendSmsRequest();
//        request.setPhoneNumbers(mobile);
//        request.setSignName(this.config.getSignName());
//        request.setTemplateCode(tplCode);
//        request.setTemplateParam(params.encode());
//        return request;
//    }
}