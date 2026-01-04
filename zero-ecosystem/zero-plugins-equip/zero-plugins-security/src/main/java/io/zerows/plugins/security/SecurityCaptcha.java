package io.zerows.plugins.security;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import io.r2mo.typed.cc.Cc;
import io.zerows.plugins.security.metadata.YmSecurityCaptcha;

import java.awt.*;
import java.util.Objects;

public class SecurityCaptcha {
    private static final Cc<Integer, SecurityCaptcha> CC_CAPTCHA = Cc.open();
    private final Cc<String, CodeGenerator> ccGenerator = Cc.openThread();
    private final Cc<String, Font> ccFont = Cc.openThread();

    private final YmSecurityCaptcha captchaConfig;

    private SecurityCaptcha(final YmSecurityCaptcha captchaConfig) {
        this.captchaConfig = captchaConfig;
    }

    static SecurityCaptcha of(final YmSecurityCaptcha captcha) {
        if (Objects.isNull(captcha)) {
            return null;
        }
        return CC_CAPTCHA.pick(() -> new SecurityCaptcha(captcha), captcha.hashCode());
    }

    public YmSecurityCaptcha captchaConfig() {
        return this.captchaConfig;
    }

    /**
     * 验证码生成器构造
     */
    public CodeGenerator captchaGenerator() {
        if (Objects.isNull(this.captchaConfig)) {
            return null;
        }
        return this.ccGenerator.pick(() -> {
            final String codeType = this.captchaConfig.getCode().getType();
            final int codeLength = this.captchaConfig.getCode().getLength();
            if ("MATH".equalsIgnoreCase(codeType)) {
                // 数学公式验证码
                return new MathGenerator(codeLength);
            }
            if ("RANDOM".equalsIgnoreCase(codeType)) {
                // 随机字符串验证码
                return new RandomGenerator(codeLength);
            }
            throw new IllegalArgumentException("[ XMOD ] ( RBAC ) 非法的验证码类型配置：" + codeType);
        });
    }

    @SuppressWarnings("all")
    public Font captchaFont() {
        if (Objects.isNull(this.captchaConfig)) {
            return null;
        }
        return this.ccFont.pick(() -> {
            final String fontName = this.captchaConfig.getFont().getName();
            final int fontWeight = this.captchaConfig.getFont().getWeight();
            final int fontSize = this.captchaConfig.getFont().getSize();
            return new Font(fontName, fontWeight, fontSize);
        });
    }
}
