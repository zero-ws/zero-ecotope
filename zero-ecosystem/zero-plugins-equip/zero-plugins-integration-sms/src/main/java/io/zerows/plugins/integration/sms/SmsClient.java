package io.zerows.plugins.integration.sms;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.sdk.plugins.InfixClient;

/**
 * AliSmsClient for app.zero.cloud of <a href="https://dysms.console.aliyun.com/dysms.htm">阿里巴巴短信服务</a>
 * Message open sdk
 */
public interface SmsClient extends InfixClient<SmsClient> {

    static SmsClient createShared(final Vertx vertx) {
        return new SmsClientImpl(vertx, SmsConfig.create());
    }

    static SmsClient createShared(final Vertx vertx, final JsonObject config) {
        return new SmsClientImpl(vertx, SmsConfig.create(config));
    }

    @Fluent
    @Override
    SmsClient init(JsonObject params);

    /**
     * Send messsage to mobile by template
     *
     * @param mobile  mobile number
     * @param tplCode default template codes
     * @param params  params for template
     *
     * @return self reference
     */
    @Fluent
    SmsClient send(String mobile, String tplCode, JsonObject params,
                   Handler<AsyncResult<JsonObject>> handler);

    default Future<JsonObject> send(final String mobile, final String tplCode, final JsonObject params) {
        final Promise<JsonObject> response = Promise.promise();
        // R2MO / 有问题
        // this.send(mobile, tplCode, params, response);
        return response.future();
    }

    default OLog logger() {
        return OLog.of(this.getClass(), "SMS/Ali");
    }
}
