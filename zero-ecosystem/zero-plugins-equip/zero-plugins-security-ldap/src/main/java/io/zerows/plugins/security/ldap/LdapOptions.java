package io.zerows.plugins.security.ldap;

import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom LDAP configuration options extending standard Vert.x capabilities.
 * Supports Search & Bind pattern and attribute mapping.
 */
@JsonGen(publicConverter = false)
@Data
@Accessors(chain = true)
public class LdapOptions {

    // 标准连接字段
    private String url;

    // 搜索模式专用 (Search & Bind)
    private String username;
    private String password;
    private String base;      // Search Base DN

    // 属性映射
    private String userEmail; // 对应配置 user-email
    private String userId;    // 对应配置 user-id

    // 高级连接选项 (新增)
    private String referral = "follow";                 // LDAP Referral 策略 (e.g., "follow", "ignore")
    private String mechanism = "SIMPLE";                // 认证机制 (e.g., "SIMPLE", "DIGEST-MD5")

    // 多策略查询模板 (支持 Direct Bind 兜底)
    private List<String> userQuery; // 对应配置 user-query

    public LdapOptions() {
        this.userQuery = new ArrayList<>();
    }

    public LdapOptions(final JsonObject json) {
        this();
        LdapOptionsConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        LdapOptionsConverter.toJson(this, json);
        return json;
    }

    // ================= Getters/Setters (Manual overrides if needed) =================

    // Lombok 已生成大部分 Getter/Setter，这里手动保留 List 的特殊处理方法

    @SuppressWarnings("UnusedReturnValue")
    public LdapOptions setUserQuery(final List<String> userQuery) {
        this.userQuery = userQuery;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LdapOptions addUserQuery(final String query) {
        if (this.userQuery == null) {
            this.userQuery = new ArrayList<>();
        }
        this.userQuery.add(query);
        return this;
    }
}