package io.zerows.plugins.security.email.metadata;

import io.r2mo.base.util.R2MO;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

@Data
public class YmSecurityEmail implements Serializable {
    private int length = 6;
    private String expiredAt = "5m";
    private String subject;
    private String template = "captcha-email";

    public Duration expiredAt() {
        return R2MO.toDuration(this.expiredAt);
    }
}
