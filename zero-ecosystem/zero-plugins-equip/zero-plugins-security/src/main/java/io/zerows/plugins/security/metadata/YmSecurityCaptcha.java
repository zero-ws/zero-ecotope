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
 *         interfereCount: 8        # 干扰线数量
 *         style:
 *           background: "#F4F6F8"  # 背景色
 *           text: "#991B1B,#9A3412,#92400E,#166534,#0F766E,#1E40AF,#6B21A8,#9D174D" # 文字颜色池
 *           interfere: "#7F1D1D,#7C2D12,#713F12,#14532D,#0F766E,#1E3A8A,#4C1D95,#831843" # 干扰线颜色池
 *           interfereAlpha: 0.35   # 干扰线透明度
 *         code:
 *           type: RANDOM           # 验证码字符串类型 MATH-算术 | RANDOM-随机字符
 *           length: 5              # 验证码字符串长度，type = 算术时表示运算位数
 *           base: 23456789ABCDEFGHJKMNPQRSTUVWXYZ # RANDOM 字符池，默认排除 0/O/1/I/L 等易混淆字符
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
     * 验证码干扰线数量。Hutool 默认值较高，登录页中会明显影响可读性。
     */
    private int interfereCount = 8;
    /**
     * 验证码视觉样式配置
     */
    private ConfigStyle style = new ConfigStyle();
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
        /**
         * 随机验证码字符池，默认排除 0/O/1/I/L 等易混淆字符。
         */
        private String base = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
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

    @Data
    public static class ConfigStyle implements Serializable {
        /**
         * 背景色
         */
        private String background = "#F4F6F8";
        /**
         * 文字颜色池，使用逗号分隔。默认只使用深色，避免白色登录界面中浅色字符不可读。
         */
        private String text = "#991B1B,#9A3412,#92400E,#166534,#0F766E,#1E40AF,#6B21A8,#9D174D";
        /**
         * 干扰线颜色
         */
        private String interfere = "#7F1D1D,#7C2D12,#713F12,#14532D,#0F766E,#1E3A8A,#4C1D95,#831843";
        /**
         * 干扰线透明度，取值 0~1。
         */
        private Float interfereAlpha = 0.35f;
    }
}
