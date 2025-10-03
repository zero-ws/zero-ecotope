package io.zerows.extension.commerce.rbac.atom;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.mbse.atom.specification.KQr;
import io.zerows.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.integrated.jackson.databind.JsonObjectSerializer;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/*
 * Security configuration data
 *
 */
public class ScConfig implements Serializable {
    private static final Cc<String, KQr> CC_KQR = Cc.open();
    // --------------- 图片验证码相关属性
    /**
     * 图片验证码长度
     */
    private Integer imageLength = 5;
    /**
     * 图片验证码过期时间（秒）
     */
    private Integer imageExpired = 30;
    /**
     * 图片验证码宽度（像素）
     */
    private Integer imageWidth = 180;
    /**
     * 图片验证码高度（像素）
     */
    private Integer imageHeight = 40;


    // ---------------- 授权码相关属性
    /**
     * 授权码超时时间（秒）
     */
    private Integer codeExpired = 30;
    /**
     * 授权码长度
     */
    private Integer codeLength = 8;


    // ---------------- 短信验证码相关属性
    /**
     * 短信码超时时间（秒）
     */
    private Integer messageExpired = 60;
    /**
     * 短信码长度
     */
    private Integer messageLength = 4;


    // ----------------- 令牌相关属性
    /**
     * 令牌超时时间（分钟）
     */
    private Long tokenExpired = 30L;


    // ----------------- 布尔开关
    /**
     * RBAC 模型中是否支持 用户组 功能，目前整个平台系统中主要支持两种带有安全实体的模型
     * <pre><code>
     *     1. 用户 - 角色（模型）
     *     2. 用户 - 用户组 - 角色（模型）
     * </code></pre>
     * 上述两种模型中，不同安全实体的关联关系如下：
     * <pre><code>
     *     结构：
     *     1. 用户 - 线性结构
     *     2. 角色 - 线性结构
     *     3. 用户组 - 树型结构
     *     关系：
     *     1. 用户 - 角色（多对多）
     *     2. 用户 - 用户组（多对多）
     *     3. 用户组 - 角色（多对多）
     * </code></pre>
     */
    private Boolean supportGroup = Boolean.FALSE;

    /**
     * 是否支持权限的二级缓存，基于 role = xxx 的方式，角色和权限的关联关系如
     * <pre><code>
     *     Role - Permission
     * </code></pre>
     * 支持权限对应的二级缓存，有了二级缓存后，权限计算的 Profile 会变得相对复杂
     */
    private Boolean supportSecondary = Boolean.FALSE;

    /**
     * 是否支持多应用模型，多应用模型和多租户模型区别
     * <pre><code>
     *     1. 多应用模型
     *        sigma, appId，相同概念
     *     2. 多租户模型
     *        sigma, tenantId，相同概念
     *        一个 sigma 会包含多个 appId
     * </code></pre>
     */
    private Boolean supportMultiApp = Boolean.TRUE;

    /**
     * 是否支持 zero-is 的集成管理模块，若支持集成管理模块，则会开启集成存储模式
     * <pre><code>
     *     1. false
     *        不支持集成管理，XAttachment 的表结构中会直接存储对应的附件或文件信息，上传内容直接存储在服务器上。
     *     2. true
     *        支持集成管理，可搭载额外的存储模块，对应 zero-is 扩展的内容。
     * </code></pre>
     */
    private Boolean supportIntegration = Boolean.FALSE;

    /**
     * 是否支持图片验证码
     * <pre><code>
     *     true: 支持图片验证码
     *     false：不支持图片验证码
     * </code></pre>
     */
    private Boolean supportCaptcha = Boolean.FALSE;


    // ----------------- 登录限制设置
    /**
     * 异常登录次数限制，如果您密码错误次数超过了此属性的设置，那么账号将被锁定
     */
    private Integer verifyLimitation = null;
    /**
     * 启用登录限制之后会启用此属性，此属性表示登录限制的时间间隔，常用属性如
     * <pre><code>
     *    verifyLimitation = 3
     *    verifyDuration = 300
     * </code></pre>
     * 上述含义表示登录限制为 3 次，账号锁定之后会设置 300 秒（5分钟）时间来解锁账号
     */
    private Integer verifyDuration = 300;


    // ----------------- 安全实体 Qr 配置
    /**
     * 用于标识安全实体的专用限制
     * 1) User，用户标识
     * 2) Role，角色标识
     * 3) Group，用户组标识
     * 4) Permission，权限标识
     * 5) Action，操作标识
     * 6) Resource，资源标识
     */
    private ScCondition condition = new ScCondition();
    /**
     * 默认初始化密码，如果您的密码是此密码，那么前端会跳转到密码修改页面
     */
    private String initializePassword;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject initialize = new JsonObject();
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject category = new JsonObject();

    public String getInitializePassword() {
        return this.initializePassword;
    }

    public void setInitializePassword(final String initializePassword) {
        this.initializePassword = initializePassword;
    }

    public ScCondition getCondition() {
        return this.condition;
    }

    public void setCondition(final ScCondition condition) {
        this.condition = condition;
    }

    public Integer getImageExpired() {
        return this.imageExpired;
    }

    public void setImageExpired(final Integer imageExpired) {
        this.imageExpired = imageExpired;
    }

    public Integer getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(final Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(final Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getImageLength() {
        return this.imageLength;
    }

    public void setImageLength(final Integer imageLength) {
        this.imageLength = imageLength;
    }

    /*
     * 默认使用秒，所以此处不转换
     */
    public Integer getCodeExpired() {
        return this.codeExpired;
    }

    public void setCodeExpired(final Integer codeExpired) {
        this.codeExpired = codeExpired;
    }

    public Integer getCodeLength() {
        return this.codeLength;
    }

    public void setCodeLength(final Integer codeLength) {
        this.codeLength = codeLength;
    }


    public Boolean getSupportSecondary() {
        return this.supportSecondary;
    }

    public void setSupportSecondary(final Boolean supportSecondary) {
        this.supportSecondary = supportSecondary;
    }

    /**
     * 默认使用分钟，所以此处分钟转秒
     *
     * @return 返回秒
     */
    public Integer getTokenExpired() {
        if (null == this.tokenExpired) {
            this.tokenExpired = 0L;
        }
        return Math.toIntExact(TimeUnit.MINUTES.toSeconds(this.tokenExpired));
    }

    public void setTokenExpired(final Long tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public Integer getMessageExpired() {
        return this.messageExpired;
    }

    public void setMessageExpired(final Integer messageExpired) {
        this.messageExpired = messageExpired;
    }

    public Integer getMessageLength() {
        return this.messageLength;
    }

    public void setMessageLength(final Integer messageLength) {
        this.messageLength = messageLength;
    }

    public Boolean getSupportCaptcha() {
        return this.supportCaptcha;
    }

    public void setSupportCaptcha(final Boolean supportCaptcha) {
        this.supportCaptcha = supportCaptcha;
    }

    public Integer getVerifyLimitation() {
        return this.verifyLimitation;
    }

    public void setVerifyLimitation(final Integer verifyLimitation) {
        this.verifyLimitation = verifyLimitation;
    }

    public Integer getVerifyDuration() {
        return this.verifyDuration;
    }

    public void setVerifyDuration(final Integer verifyDuration) {
        this.verifyDuration = verifyDuration;
    }

    public Boolean getSupportGroup() {
        return this.supportGroup;
    }

    public void setSupportGroup(final Boolean supportGroup) {
        this.supportGroup = supportGroup;
    }

    public Boolean getSupportMultiApp() {
        return this.supportMultiApp;
    }

    public void setSupportMultiApp(final Boolean supportMultiApp) {
        this.supportMultiApp = supportMultiApp;
    }

    public JsonObject getCategory() {
        return this.category;
    }

    public void setCategory(final JsonObject category) {
        this.category = category;
    }

    public Boolean getSupportIntegration() {
        return this.supportIntegration;
    }

    public void setSupportIntegration(final Boolean supportIntegration) {
        this.supportIntegration = supportIntegration;
    }

    public KQr category(final String name) {
        return CC_KQR.pick(() -> {
            final JsonObject serializeJ = Ut.valueJObject(this.category, name);
            final KQr qr = Ut.deserialize(serializeJ, KQr.class);
            if (qr.valid()) {
                return qr.identifier(name);
            } else {
                return null;
            }
        }, name);
    }

    public JsonObject getInitialize() {
        return this.initialize;
    }

    public void setInitialize(final JsonObject initialize) {
        this.initialize = initialize;
    }

    @Override
    public String toString() {
        return "ScConfig{" +
            ", condition=" + this.condition +
            ", codeExpired=" + this.codeExpired +
            ", codeLength=" + this.codeLength +
            ", tokenExpired=" + this.tokenExpired +
            ", supportGroup=" + this.supportGroup +
            ", supportSecondary=" + this.supportSecondary +
            ", supportMultiApp=" + this.supportMultiApp +
            ", supportIntegration=" + this.supportIntegration +
            ", verifyCode=" + this.supportCaptcha +
            ", verifyLimitation=" + this.verifyLimitation +
            ", verifyDuration=" + this.verifyDuration +
            ", initializePassword='" + this.initializePassword + '\'' +
            ", initialize=" + this.initialize +
            ", category=" + this.category +
            '}';
    }
}
