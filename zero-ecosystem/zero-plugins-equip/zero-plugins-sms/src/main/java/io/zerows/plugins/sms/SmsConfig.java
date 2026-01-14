package io.zerows.plugins.sms;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

public class SmsConfig implements Serializable {
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


    @Getter
    private final String accessId;
    @Getter
    private final String accessSecret;
    @Getter
    private final String signName;
    private final JsonObject tpl;
    private String endpoint;

    private SmsConfig(final HConfig configJ) {
        JsonObject options = configJ.options();
        JsonObject smsConfig = options.getJsonObject("aliyun");
        this.accessId = Ut.valueString(smsConfig, KEY_ID);
        this.accessSecret = Ut.valueString(smsConfig, KEY_SECRET);
        this.signName = Ut.valueString(smsConfig, KEY_SIGN_NAME);
        this.tpl = Ut.valueJObject(smsConfig, KEY_TPL);
        if (null == this.endpoint) {
            this.endpoint = DFT_DOMAIN;
        }
    }

    static SmsConfig create(final HConfig config) {
        return new SmsConfig(config);
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