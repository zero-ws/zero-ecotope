package io.zerows.plugins.sms;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;

import java.util.Set;

/**
 * AliSmsClient for app.zero.cloud of <a href="https://dysms.console.aliyun.com/dysms.htm">阿里巴巴短信服务</a>
 * Message open sdk
 */
@AddOn.Name("DEFAULT_SMS_CLIENT")
public interface SmsClient {
    static SmsClient createClient(final Vertx vertx, final SmsConfig config) {
        return new SmsClientImpl(vertx, config);
    }

    default Future<JsonObject> sendAsync(final String tplCode, final JsonObject params, final String to) {
        return this.sendAsync(tplCode, params, Set.of(to));
    }

    Future<JsonObject> sendAsync(final String tplCode, final JsonObject params, final Set<String> toSet);
}