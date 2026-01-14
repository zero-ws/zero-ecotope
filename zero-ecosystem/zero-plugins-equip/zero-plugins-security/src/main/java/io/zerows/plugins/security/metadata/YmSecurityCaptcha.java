package io.zerows.plugins.security.metadata;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * 对应配置位置：
 * <pre>
 *     security:
 *       captcha:
 *         enabled: true            # 是否启用验证码
 *         type:                    # 验证码类型
 *         expiredAt: 60            # 验证码过期时间，单位：秒
 *         width: 220               # 验证码图片宽度
 *         height: 80               # 验证码图片高度
 *         textAlpha: 0.8           # 验证码图片透明度
 *         code:
 *           type: RANDOM           # 验证码字符串类型 MATH-算术 | RANDOM-随机字符
 *           length: 5              # 验证码字符串长度，type = 算术时表示运算位数
 *         font:
 *           name: PingFang SC      # 字体名称
 *           weight: 1              # 字体样式：0-正常 | 1-粗体 | 2-斜体 | 3-粗斜体
 *           size: 32               # 字体大小
 * </pre>
 *
 * @author lang : 2025-12-30
 */
@Data
public class YmSecurityCaptcha implements Serializable {
    private boolean enabled = Boolean.FALSE;
    private TypeLogin type = TypeLogin.CAPTCHA;
    /**
     * 验证码过期时间，单位：30秒，此处之前有个问题就是 captcha 图片验证码的时间是 0s
     * 0s 会导致 {@link _401UnauthorizedException} 的过期异常信息。
     */
    private String expiredAt = "30s";
    /**
     * 验证码图片宽度
     */
    private int width = 180;
    /**
     * 验证码图片高度
     */
    private int height = 48;
    /**
     * 验证码图片透明度
     */
    private Float textAlpha = 0.8f;
    /**
     * 验证码字符配置
     */
    private ConfigCode code = new ConfigCode();
    /**
     * 验证码字体配置
     */
    private ConfigFont font = new ConfigFont();

    public CaptchaArgs forArguments() {
        final Duration duration = R2MO.toDuration(this.expiredAt);
        return CaptchaArgs.of(TypeLogin.CAPTCHA, duration);
    }

    @Data
    public static class ConfigCode implements Serializable {
        /**
         * 验证码字符串类型 MATH-算术 | RANDOM-随机字符
         */
        private String type = "RANDOM";
        /**
         * 验证码字符串长度，type = 算术时表示运算位数
         */
        private int length = 5;
    }

    @Data
    public static class ConfigFont implements Serializable {
        /**
         * 字体名称
         */
        private String name = "PingFang SC";
        /**
         * 字体样式：0-正常 | 1-粗体 | 2-斜体 | 3-粗斜体
         */
        private int weight = 1;

        /**
         * 字体大小
         */
        private int size = 36;
    }
}
