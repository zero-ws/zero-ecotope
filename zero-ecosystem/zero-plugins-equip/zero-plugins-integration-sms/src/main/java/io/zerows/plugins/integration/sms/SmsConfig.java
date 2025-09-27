package io.zerows.plugins.integration.sms;

import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.zdk.plugins.InfixConfig;

import java.io.Serializable;
import java.util.Objects;

public class SmsConfig implements Serializable {

    static final String CONFIG_KEY = "sms-ali";
    static final String TIMEOUT_CONN = "timeout_connect";
    static final String TIMEOUT_READ = "timeout_read";
    static final String DFT_PRODUCT = "Dysmsapi";
    static final String DFT_REGION = "cn-hangzhou";
    static final String RESPONSE_REQUEST_ID = "request_id";
    static final String RESPONSE_BUSINESS_ID = "business_id";
    static final String RESPONSE_CODE = "code";
    static final String RESPONSE_MESSAGE = "message";
    private static final String KEY_ID = "access_id";
    private static final String KEY_SECRET = "access_secret";
    private static final String KEY_SIGN_NAME = "sign_name";
    private static final String KEY_TPL = "tpl";
    private static final String DFT_DOMAIN = "dysmsapi.aliyuncs.com";

    private static final InfixConfig CONFIG = InfixConfig.create(CONFIG_KEY, CONFIG_KEY);

    private final String accessId;
    private final String accessSecret;
    private final String signName;
    private final JsonObject tpl;
    private String endpoint;

    private SmsConfig(final JsonObject configJ) {
        this.accessId = Ut.valueString(configJ, KEY_ID);
        this.accessSecret = Ut.valueString(configJ, KEY_SECRET);
        this.signName = Ut.valueString(configJ, KEY_SIGN_NAME);
        this.tpl = Ut.valueJObject(configJ, KEY_TPL);
        this.endpoint = CONFIG.getEndPoint();
        if (null == this.endpoint) {
            this.endpoint = DFT_DOMAIN;
        }
    }

    static SmsConfig create(final JsonObject config) {
        return new SmsConfig(config);
    }

    static SmsConfig create() {
        return new SmsConfig(CONFIG.getConfig());
    }

    public JsonObject getConfig() {
        return CONFIG.getConfig();
    }

    public String getAccessId() {
        return this.accessId;
    }

    public String getAccessSecret() {
        return this.accessSecret;
    }

    public String getSignName() {
        return this.signName;
    }

    public String getDomain() {
        return this.endpoint;
    }

    @SuppressWarnings("all")
    public String getTpl(final String key) {
        return this.tpl.getString(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SmsConfig smsConfig)) {
            return false;
        }
        return Objects.equals(this.accessId, smsConfig.accessId) &&
            Objects.equals(this.accessSecret, smsConfig.accessSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accessId, this.accessSecret);
    }
}
