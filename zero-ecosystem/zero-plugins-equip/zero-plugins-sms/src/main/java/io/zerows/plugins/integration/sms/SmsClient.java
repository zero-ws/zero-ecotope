package io.zerows.plugins.integration.sms;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;

/**
 * AliSmsClient for app.zero.cloud of <a href="https://dysms.console.aliyun.com/dysms.htm">阿里巴巴短信服务</a>
 * Message open sdk
 */
@AddOn.Name("DEFAULT_SMS_CLIENT")
public interface SmsClient  {
    static SmsClient createClient(final Vertx vertx, HConfig config) {
        return new SmsClientImpl(vertx, SmsConfig.create(config));
    }


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

    default LogO logger() {
        return LogO.of(this.getClass(), "SMS/Ali");
    }
}