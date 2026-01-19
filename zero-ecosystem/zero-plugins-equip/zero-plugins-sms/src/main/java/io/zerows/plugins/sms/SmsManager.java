package io.zerows.plugins.sms;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.sms.exception._80351Exception404SmsAccessId;
import io.zerows.plugins.sms.exception._80352Exception404SmsAccessSecret;
import io.zerows.plugins.sms.exception._80353Exception404SmsSignName;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

/**
 * @author lang : 2025-10-17
 */
class SmsManager extends AddOnManager<SmsClient> {

    private static final Cc<String, SmsClient> CC_STORED = Cc.open();
    private static final Cc<String, SmsConfig> CC_CONFIG = Cc.open();

    private static final SmsManager INSTANCE = new SmsManager();

    private SmsManager() {
    }

    static SmsManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, SmsClient> stored() {
        return CC_STORED;
    }

    SmsConfig configOf(final Vertx vertx, final HConfig config) {
        return CC_CONFIG.pick(() -> {
            final JsonObject smsJ = config.options();
            final SmsConfig smsConfig = Ut.deserialize(smsJ, SmsConfig.class);
            final String accessId = smsConfig.getAccessId();
            // Access ID 检查
            Fn.jvmKo(StrUtil.isEmpty(accessId), _80351Exception404SmsAccessId.class);

            final String accessSecret = smsConfig.getAccessSecret();
            // Access Secret 检查
            Fn.jvmKo(StrUtil.isEmpty(accessSecret), _80352Exception404SmsAccessSecret.class);

            final String signName = smsConfig.getSignName();
            // 短信签名检查
            Fn.jvmKo(StrUtil.isEmpty(signName), _80353Exception404SmsSignName.class);

            return smsConfig;
        }, String.valueOf(vertx.hashCode()));
    }

    SmsConfig configOf(final Vertx vertx) {
        return CC_CONFIG.get(String.valueOf(vertx.hashCode()));
    }
}