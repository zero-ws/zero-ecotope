package io.zerows.plugins.email;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.xync.email.EmailCredential;
import io.r2mo.xync.email.EmailDomain;
import io.r2mo.xync.email.EmailProtocol;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * <pre>
 * email:
 * # 全局配置
 * username:
 * password:
 * encoding:
 * # 发送协议
 * smtp:
 * host: ...
 * # 接受协议
 * imap: ...
 * pop3: ...
 * </pre>
 *
 * @author lang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EmailConfig implements Serializable {

    // 2. 各个协议的配置域 (预先初始化，固定协议)
    // 注意：不要加 @JsonProperty 或 @JsonSetter，防止反序列化工具尝试覆盖这些实例
    private final EmailDomain smtp = new EmailDomain(EmailProtocol.SMTP);
    private final EmailDomain imap = new EmailDomain(EmailProtocol.IMAP);
    private final EmailDomain pop3 = new EmailDomain(EmailProtocol.POP3);
    // 1. 全局默认配置
    private String username;
    private String password;
    private String encoding = "UTF-8";

    /**
     * 默认构造函数
     */
    public EmailConfig() {
    }

    /**
     * 核心构造函数：接收 JsonObject 配置并手动绑定
     * 替代了 Spring 的 Binder 逻辑
     *
     * @param options 通常来自 Vert.x 的 config()
     */
    public EmailConfig(final JsonObject options) {
        this.bind(options.getMap());
    }

    /**
     * 手动绑定逻辑
     *
     * @param rawProps 配置 Map
     */
    public void bind(final Map<String, Object> rawProps) {
        if (MapUtil.isEmpty(rawProps)) {
            return;
        }

        // 3.1 绑定全局属性
        if (rawProps.containsKey("username")) {
            this.setUsername(Convert.toStr(rawProps.get("username")));
        }
        if (rawProps.containsKey("password")) {
            this.setPassword(Convert.toStr(rawProps.get("password")));
        }
        if (rawProps.containsKey("encoding")) {
            this.setEncoding(Convert.toStr(rawProps.get("encoding")));
        }

        // 3.2 绑定协议域
        // 关键点：这里手动获取 Map 并调用 Domain 自身的 bind，规避了 EmailDomain 无参构造的问题
        this.smtp.bind(this.getMapOrEmpty(rawProps, "smtp"));
        this.imap.bind(this.getMapOrEmpty(rawProps, "imap"));
        this.pop3.bind(this.getMapOrEmpty(rawProps, "pop3"));
    }

    /**
     * 辅助方法：安全获取 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapOrEmpty(final Map<String, Object> parent, final String key) {
        if (parent.containsKey(key)) {
            final Object value = parent.get(key);
            if (value instanceof Map) {
                return (Map<String, Object>) value;
            } else if (value instanceof JsonObject) {
                return ((JsonObject) value).getMap();
            }
        }
        return Collections.emptyMap();
    }

    // ==================== 业务方法 ====================

    public EmailCredential getCredential() {
        final EmailCredential credential = new EmailCredential();
        credential.username(this.getUsername());
        credential.password(this.getPassword());
        return credential;
    }

    public EmailDomain getSender() {
        return this.smtp;
    }

    public EmailDomain getReceiver() {
        if (StrUtil.isNotEmpty(this.pop3.getHost())) {
            return this.pop3;
        }
        return this.imap;
    }

    // ==================== 兼容 Jackson 反序列化 (可选) ====================
    // 如果你依然需要支持 json.mapTo(EmailConfig.class)，添加以下 Setter 欺骗 Jackson

    public void setSmtp(final Map<String, Object> map) {
        this.smtp.bind(map);
    }

    public void setImap(final Map<String, Object> map) {
        this.imap.bind(map);
    }

    public void setPop3(final Map<String, Object> map) {
        this.pop3.bind(map);
    }
}