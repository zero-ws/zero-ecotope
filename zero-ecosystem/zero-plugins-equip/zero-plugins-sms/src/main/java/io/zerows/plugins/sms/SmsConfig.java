package io.zerows.plugins.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.xync.sms.SmsContext;
import io.r2mo.xync.sms.SmsCredential;
import lombok.Data;

import java.io.Serializable;

@Data
public class SmsConfig implements Serializable {

    @JsonProperty(SmsContext.KEY_ACCESS_ID)
    private String accessId;
    @JsonProperty(SmsContext.KEY_ACCESS_SECRET)
    private String accessSecret;
    @JsonProperty(SmsContext.KEY_SIGN_NAME)
    private String signName;
    @JsonProperty(SmsContext.KEY_TIMEOUT_CONNECT)
    private int timeoutConnect = 5000;
    @JsonProperty(SmsContext.KEY_TIMEOUT_READ)
    private int timeoutRead = 10000;
    @JsonProperty(SmsContext.KEY_REGION)
    private String region;
    @JsonProperty(SmsContext.KEY_HOST)
    private String host;

    public SmsCredential getCredential() {
        final SmsCredential credential = new SmsCredential();
        credential.accessId(this.accessId);
        credential.accessSecret(this.accessSecret);
        return credential;
    }
}