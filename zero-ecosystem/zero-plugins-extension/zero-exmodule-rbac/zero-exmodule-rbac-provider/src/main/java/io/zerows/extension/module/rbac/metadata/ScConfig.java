package io.zerows.extension.module.rbac.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.hashing.HashingStrategy;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.MDConfig;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.mbse.metadata.KQr;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/*
 * Security configuration data
 *
 */
@Data
public class ScConfig implements MDConfig {
    private static final Cc<String, KQr> CC_KQR = Cc.open();

    // ----------------- 令牌相关属性
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


    // ----------------- 布尔开关
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
     *        sigma, id，相同概念
     *     2. 多租户模型
     *        sigma, tenantId，相同概念
     *        一个 sigma 会包含多个 id
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

    // ----------------- 默认值系统

    private Default valueDefault;

    private Resource valueResource;

    // ----------------- 登录限制设置
    /**
     * 异常登录次数限制，如果您密码错误次数超过了此属性的设置，那么账号将被锁定
     */
    private Integer verifyLimitation = 3;
    /**
     * 启用登录限制之后会启用此属性，此属性表示登录限制的时间间隔，常用属性如
     * <pre><code>
     *    verifyLimitation = 3
     *    verifyDuration = 300
     * </code></pre>
     * 上述含义表示登录限制为 3 次，账号锁定之后会设置 300 秒（5分钟）时间来解锁账号
     */
    private Integer verifyDuration = 300;
    /**
     * 用于标识安全实体的专用限制
     * 1) User，用户标识
     * 2) Role，角色标识
     * 3) Group，用户组标识
     * 4) Permission，权限标识
     * 5) Action，操作标识
     * 6) Resource，资源标识
     */
    private Condition condition = new Condition();


    // ----------------- 安全实体 Qr 配置
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject initialize = new JsonObject();
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject category = new JsonObject();

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

    @Override
    public String toString() {
        return "ScConfig{" +
            ", condition=" + this.condition +
            ", supportGroup=" + this.supportGroup +
            ", supportSecondary=" + this.supportSecondary +
            ", supportMultiApp=" + this.supportMultiApp +
            ", supportIntegration=" + this.supportIntegration +
            ", verifyLimitation=" + this.verifyLimitation +
            ", verifyDuration=" + this.verifyDuration +
            ", initialize=" + this.initialize +
            ", category=" + this.category +
            ", valueDefault=" + this.valueDefault +
            '}';
    }

    @Data
    public static class Resource implements Serializable {
        private String menu = "91a78ce8-30c7-4894-b235-730eb3e61255";
    }

    @Data
    public static class Condition implements Serializable {
        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray user = new JsonArray().add(KName.SIGMA);

        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray role = new JsonArray().add(KName.SIGMA);

        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray group = new JsonArray().add(KName.SIGMA);
        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray action = new JsonArray().add(KName.SIGMA);
        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray permission = new JsonArray().add(KName.SIGMA);
        @JsonSerialize(using = JsonArraySerializer.class)
        @JsonDeserialize(using = JsonArrayDeserializer.class)
        private JsonArray resource = new JsonArray().add(KName.SIGMA);

        @Override
        public String toString() {
            return "ScCondition{" +
                "user=" + this.user +
                ", role=" + this.role +
                ", group=" + this.group +
                ", action=" + this.action +
                ", permission=" + this.permission +
                ", resource=" + this.resource +
                '}';
        }
    }

    @Data
    @ToString
    public static class Default implements Serializable {
        // 加密过的默认密码
        private String userPassword;

        // 角色权限 CODE
        private Set<String> rolePermissions = new HashSet<>();
        // 角色菜单 NAME
        private Set<String> roleMenus = new HashSet<>();

        private static final HashingStrategy STRATEGY = HashingStrategy.load();
        private static final String DEFAULT_ALG = "sha512";
        private static final String DEFAULT_PASSWORD = "12345678";
    }

    public static String defaultPassword() {
        return Default.STRATEGY.hash(Default.DEFAULT_ALG, null, null, Default.DEFAULT_PASSWORD);
    }
}
