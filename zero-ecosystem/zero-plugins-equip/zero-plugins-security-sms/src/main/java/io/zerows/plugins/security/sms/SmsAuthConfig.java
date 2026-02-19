package io.zerows.plugins.security.sms;

import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

@Data
public class SmsAuthConfig implements Serializable {
    private int length = 6;
    private int expiredAt = 60;
    private String template;
    private boolean image = false;

    public Duration expiredAt() {
        return Duration.ofSeconds(this.expiredAt);
    }
}
