package io.zerows.plugins.security.email;

import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

@Data
public class EmailAuthConfig implements Serializable {
    private int length = 6;
    private int expiredAt = 300;
    private String subject;
    private String template = "captcha-email";

    public Duration expiredAt() {
        return Duration.ofSeconds(this.expiredAt);
    }
}
