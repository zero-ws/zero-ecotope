package io.zerows.plugins.security.service;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.cc.Cc;
import io.zerows.plugins.security.metadata.YmSecurityCaptcha;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CaptchaConfig {
    private static final Cc<Integer, CaptchaConfig> CC_CAPTCHA = Cc.open();
    private final Cc<String, CodeGenerator> ccGenerator = Cc.openThread();
    private final Cc<String, Font> ccFont = Cc.openThread();
    private final Cc<String, Color> ccColor = Cc.openThread();
    private final Cc<String, List<Color>> ccPalette = Cc.openThread();

    private final YmSecurityCaptcha captchaConfig;

    private CaptchaConfig(final YmSecurityCaptcha captchaConfig) {
        this.captchaConfig = captchaConfig;
    }

    public static CaptchaConfig of(final YmSecurityCaptcha captcha) {
        if (Objects.isNull(captcha)) {
            return null;
        }
        return CC_CAPTCHA.pick(() -> new CaptchaConfig(captcha), captcha.hashCode());
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
                final String base = this.captchaConfig.getCode().getBase();
                return StrUtil.isBlank(base) ? new RandomGenerator(codeLength) : new RandomGenerator(base, codeLength);
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

    public Color backgroundColor() {
        return this.colorOf(this.style().getBackground(), new YmSecurityCaptcha.ConfigStyle().getBackground());
    }

    public Color interfereColor() {
        return this.colorOf(this.style().getInterfere(), new YmSecurityCaptcha.ConfigStyle().getInterfere());
    }

    public Color textColor() {
        return this.colorOf(this.style().getText(), new YmSecurityCaptcha.ConfigStyle().getText());
    }

    public List<Color> interferePalette() {
        final String configured = this.style().getInterfere();
        return this.ccPalette.pick(() -> this.paletteOf(configured, new YmSecurityCaptcha.ConfigStyle().getInterfere()),
            "interfere:" + configured);
    }

    public List<Color> textPalette() {
        final String configured = this.style().getText();
        return this.ccPalette.pick(() -> this.paletteOf(configured, new YmSecurityCaptcha.ConfigStyle().getText()), "text:" + configured);
    }

    public Float interfereAlpha() {
        final Float alpha = this.style().getInterfereAlpha();
        return Objects.isNull(alpha) ? new YmSecurityCaptcha.ConfigStyle().getInterfereAlpha() : alpha;
    }

    private List<Color> paletteOf(final String value, final String fallback) {
        return Arrays.stream(StrUtil.blankToDefault(value, fallback).split(","))
            .map(String::trim)
            .filter(StrUtil::isNotBlank)
            .map(color -> this.colorOf(color, fallback))
            .toList();
    }

    private Color colorOf(final String value, final String fallback) {
        final String normalized = StrUtil.blankToDefault(value, fallback).trim().split(",")[0].trim();
        return this.ccColor.pick(() -> Color.decode(normalized), normalized);
    }

    private YmSecurityCaptcha.ConfigStyle style() {
        return Objects.isNull(this.captchaConfig.getStyle()) ? new YmSecurityCaptcha.ConfigStyle() : this.captchaConfig.getStyle();
    }
}
